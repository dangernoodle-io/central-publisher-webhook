/**
 * # Central Publisher Webhook Terraform Module
 *
 * ```tf
 * module "webhook" {
 *   source = "git@github.com:dangernoodle-io/central-publisher-webhook.git//src/main/terraform??ref=X.Y.Z"
 *
 * }
 * ```
 */
locals {
  has_domain = var.fqdn.domain != null
  has_path   = var.publisher.path != null
  has_pwd    = var.credentials.password != null

  jar_hash = local.has_path ? filebase64sha256(var.publisher.path) : data.maven_artifact.publisher[0].output_base64sha256
  jar_path = local.has_path ? var.publisher.path : data.maven_artifact.publisher[0].output_path
}

data "aws_iam_policy_document" "assume" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

data "aws_iam_policy_document" "publisher" {
  statement {
    effect = "Allow"

    actions = [
      "logs:CreateLogStream"
    ]

    resources = [
      "arn:aws:logs:*:*:log-group:/aws/lambda/${var.name}"
    ]
  }

  statement {
    effect = "Allow"

    actions = [
      "logs:CreateLogStream",
      "logs:PutLogEvents",
    ]

    resources = [
      "arn:aws:logs:*:*:log-group:/aws/lambda/${var.name}:log-stream:*"
    ]
  }

  statement {
    effect = "Allow"

    actions = [
      "ssm:GetParameter",
      "kms:Decrypt"
    ]

    # TODO: fix this
    resources = ["*"]
  }
}

data "aws_route53_zone" "zone" {
  count = local.has_domain ? 1 : 0
  name  = "${var.fqdn.domain}."
}

data "maven_artifact" "publisher" {
  count       = var.publisher.path == null ? 1 : 0
  artifact_id = "central-publisher-webhook"
  classifier  = "shaded"
  group_id    = "io.dangernoodle"
  version     = var.publisher.version
  output_dir  = "${path.module}/.terraform/central"
}

# iam

resource "aws_iam_role" "publisher" {
  name               = var.name
  assume_role_policy = data.aws_iam_policy_document.assume.json
}

resource "aws_iam_policy" "publisher" {
  name        = var.name
  path        = "/"
  description = "IAM policy for ${var.name}"
  policy      = data.aws_iam_policy_document.publisher.json
}

resource "aws_iam_role_policy_attachment" "publisher" {
  role       = aws_iam_role.publisher.name
  policy_arn = aws_iam_policy.publisher.arn
}

# parameter store

resource "random_password" "password" {
  count   = local.has_pwd ? 0 : 1
  length  = var.credentials.pwd_length
  special = false
}

resource "aws_ssm_parameter" "central_password" {
  name  = "${var.ssm_prefix}/central-password"
  type  = "SecureString"
  value = local.has_pwd ? sensitive(var.credentials.password) : random_password.password[0].result
}

resource "aws_ssm_parameter" "central_username" {
  name  = "${var.ssm_prefix}/central-username"
  type  = "String"
  value = var.credentials.username
}

resource "aws_ssm_parameter" "slack_app_token" {
  name  = "${var.ssm_prefix}/slack-app-token"
  type  = "SecureString"
  value = sensitive(var.slack.token)
}

resource "aws_ssm_parameter" "slack_channel" {
  name  = "${var.ssm_prefix}/slack-channel"
  type  = "String"
  value = var.slack.channel
}

# lambda

resource "aws_cloudwatch_log_group" "lambda" {
  name              = "/aws/lambda/${var.name}"
  retention_in_days = var.log_retention.lambda
}

resource "aws_lambda_function" "publisher" {
  filename         = local.jar_path
  function_name    = var.name
  handler          = "io.dangernoodle.cpw.WebhookHandler"
  layers           = [var.ps_ext_arn]
  memory_size      = 512
  role             = aws_iam_role.publisher.arn
  runtime          = "java21"
  timeout          = 10
  source_code_hash = local.jar_hash

  environment {
    variables = {
      CENTRAL_USERNAME = aws_ssm_parameter.central_username.name
      CENTRAL_PASSWORD = aws_ssm_parameter.central_password.name
      SLACK_CHANNEL    = aws_ssm_parameter.slack_channel.name
      SLACK_APP_TOKEN  = aws_ssm_parameter.slack_app_token.name
      TZ               = var.timezone
    }
  }

  depends_on = [
    aws_cloudwatch_log_group.lambda,
    aws_iam_role_policy_attachment.publisher
  ]
}

resource "aws_lambda_permission" "publisher" {
  statement_id  = "allowInvokeFromAPIGatewayRoute"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.publisher.arn
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.publisher.execution_arn}/*/*/*"
}

# ssl cert

resource "aws_acm_certificate" "certificate" {
  count             = local.has_domain ? 1 : 0
  domain_name       = "${var.fqdn.subdomain}.${data.aws_route53_zone.zone[0].name}"
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "certificate" {
  count                   = local.has_domain ? 1 : 0
  certificate_arn         = aws_acm_certificate.certificate[0].arn
  validation_record_fqdns = [for record in aws_route53_record.certificate : record.fqdn]
}

resource "aws_route53_record" "certificate" {
  for_each = local.has_domain ? {
    for dvo in aws_acm_certificate.certificate[0].domain_validation_options :
    dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }
  } : {}

  name    = each.value.name
  records = [each.value.record]
  ttl     = 60
  type    = each.value.type
  zone_id = data.aws_route53_zone.zone[0].zone_id
}

# api gateway

resource "aws_cloudwatch_log_group" "gateway" {
  name              = "/aws/apigateway/${var.name}"
  retention_in_days = var.log_retention.gateway
}

resource "aws_apigatewayv2_api" "publisher" {
  disable_execute_api_endpoint = local.has_domain ? true : false
  name                         = var.name
  protocol_type                = "HTTP"
}

resource "aws_apigatewayv2_domain_name" "publisher" {
  count       = local.has_domain ? 1 : 0
  domain_name = aws_acm_certificate.certificate[0].domain_name

  domain_name_configuration {
    certificate_arn = aws_acm_certificate_validation.certificate[0].certificate_arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }
}

resource "aws_apigatewayv2_integration" "publisher" {
  api_id             = aws_apigatewayv2_api.publisher.id
  integration_type   = "AWS_PROXY"
  integration_method = "POST"
  integration_uri    = aws_lambda_function.publisher.invoke_arn
}

resource "aws_apigatewayv2_api_mapping" "publisher" {
  count       = local.has_domain ? 1 : 0
  api_id      = aws_apigatewayv2_api.publisher.id
  domain_name = aws_apigatewayv2_domain_name.publisher[0].id
  stage       = aws_apigatewayv2_stage.publisher.id
}

resource "aws_apigatewayv2_route" "publisher" {
  api_id    = aws_apigatewayv2_api.publisher.id
  route_key = "POST ${var.webhook_path}"
  target    = "integrations/${aws_apigatewayv2_integration.publisher.id}"
}

resource "aws_apigatewayv2_stage" "publisher" {
  api_id      = aws_apigatewayv2_api.publisher.id
  auto_deploy = true
  name        = "$default"

  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.gateway.arn
    format = jsonencode({
      authorizerError       = "$context.authorizer.error",
      domainName            = "$context.domainName"
      domainPrefix          = "$context.domainPrefix"
      httpMethod            = "$context.httpMethod",
      identitySourceIP      = "$context.identity.sourceIp",
      integrationError      = "$context.integration.error",
      integrationLatency    = "$context.integration.latency",
      integrationRequestId  = "$context.integration.requestId",
      integrationStatus     = "$context.integration.integrationStatus",
      integrationStatusCode = "$context.integration.status",
      requestErrorMessage   = "$context.error.message",
      requestId             = "$context.requestId",
      requestTime           = "$context.requestTime"
      responseLatency       = "$context.responseLatency"
      routeKey              = "$context.routeKey",
    })
  }

  default_route_settings {
    detailed_metrics_enabled = true
    throttling_burst_limit   = 1
    throttling_rate_limit    = 1
  }
}

# route53 alias

resource "aws_route53_record" "publisher" {
  count   = local.has_domain ? 1 : 0
  name    = aws_apigatewayv2_domain_name.publisher[0].domain_name
  type    = "A"
  zone_id = data.aws_route53_zone.zone[0].zone_id

  alias {
    name                   = aws_apigatewayv2_domain_name.publisher[0].domain_name_configuration[0].target_domain_name
    zone_id                = aws_apigatewayv2_domain_name.publisher[0].domain_name_configuration[0].hosted_zone_id
    evaluate_target_health = false
  }
}
