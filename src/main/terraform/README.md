# Central Publisher Webhook Terraform Module

```tf
module "central_publisher_webhook" {
  source        = "git@github.com:dangernoodle-io/central-publisher-webhook.git//src/main/terraform??ref=X.Y.Z"
  ps_ext_arn    = "arn:aws:lambda:us-west-2:345057560386:layer:AWS-Parameters-and-Secrets-Lambda-Extension:17"
  timezone      = "America/Denver"

  artifact = {
   version = "X.Y.Z"
  }

  slack = {
   channel = "slack-channel-name"
   token   = "<SLACK_TOKEN>"
  }
}
```

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | ~> 5 |
| <a name="requirement_maven"></a> [maven](#requirement\_maven) | ~> 0.2 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | ~> 5 |
| <a name="provider_maven"></a> [maven](#provider\_maven) | ~> 0.2 |
| <a name="provider_random"></a> [random](#provider\_random) | n/a |

## Modules

No modules.

## Resources

| Name | Type |
|------|------|
| [aws_acm_certificate.certificate](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acm_certificate) | resource |
| [aws_acm_certificate_validation.certificate](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/acm_certificate_validation) | resource |
| [aws_apigatewayv2_api.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/apigatewayv2_api) | resource |
| [aws_apigatewayv2_api_mapping.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/apigatewayv2_api_mapping) | resource |
| [aws_apigatewayv2_domain_name.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/apigatewayv2_domain_name) | resource |
| [aws_apigatewayv2_integration.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/apigatewayv2_integration) | resource |
| [aws_apigatewayv2_route.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/apigatewayv2_route) | resource |
| [aws_apigatewayv2_stage.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/apigatewayv2_stage) | resource |
| [aws_cloudwatch_log_group.gateway](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/cloudwatch_log_group) | resource |
| [aws_cloudwatch_log_group.lambda](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/cloudwatch_log_group) | resource |
| [aws_iam_policy.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_policy) | resource |
| [aws_iam_role.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role) | resource |
| [aws_iam_role_policy_attachment.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/iam_role_policy_attachment) | resource |
| [aws_lambda_function.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lambda_function) | resource |
| [aws_lambda_permission.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lambda_permission) | resource |
| [aws_route53_record.certificate](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route53_record) | resource |
| [aws_route53_record.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/route53_record) | resource |
| [aws_ssm_parameter.central_password](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ssm_parameter) | resource |
| [aws_ssm_parameter.central_username](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ssm_parameter) | resource |
| [aws_ssm_parameter.slack_app_token](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ssm_parameter) | resource |
| [aws_ssm_parameter.slack_channel](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/ssm_parameter) | resource |
| [random_password.password](https://registry.terraform.io/providers/hashicorp/random/latest/docs/resources/password) | resource |
| [aws_iam_policy_document.assume](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.publisher](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/iam_policy_document) | data source |
| [aws_route53_zone.zone](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/data-sources/route53_zone) | data source |
| [maven_artifact.publisher](https://registry.terraform.io/providers/kota65535/maven/latest/docs/data-sources/artifact) | data source |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_artifact"></a> [artifact](#input\_artifact) | Jar artifact | <pre>object({<br/>    path    = optional(string, null)<br/>    version = string<br/>  })</pre> | n/a | yes |
| <a name="input_credentials"></a> [credentials](#input\_credentials) | Credentials for basic auth | <pre>object({<br/>    password   = optional(string, null)<br/>    pwd_length = optional(number, 16)<br/>    username   = optional(string, "central-publisher")<br/>  })</pre> | `{}` | no |
| <a name="input_fqdn"></a> [fqdn](#input\_fqdn) | Custom webhook domain | <pre>object({<br/>    domain    = optional(string, null)<br/>    subdomain = optional(string, "central-publisher")<br/>  })</pre> | `{}` | no |
| <a name="input_log_retention"></a> [log\_retention](#input\_log\_retention) | Log retention (in days) | <pre>object({<br/>    gateway = optional(number, 14)<br/>    lambda  = optional(number, 14)<br/>  })</pre> | `{}` | no |
| <a name="input_name"></a> [name](#input\_name) | Lambda / Role name | `string` | `"central-publisher-webhook"` | no |
| <a name="input_ps_ext_arn"></a> [ps\_ext\_arn](#input\_ps\_ext\_arn) | Parameters and secrets lambda extension arn | `string` | n/a | yes |
| <a name="input_pstore_prefix"></a> [pstore\_prefix](#input\_pstore\_prefix) | Parameter store prefix | `string` | `"central-publisher-webhook"` | no |
| <a name="input_slack"></a> [slack](#input\_slack) | Slack configuration | <pre>object({<br/>    channel = string<br/>    token   = string<br/>  })</pre> | n/a | yes |
| <a name="input_timezone"></a> [timezone](#input\_timezone) | Timezone for lambda execution environment (eg: America/Denver) | `string` | n/a | yes |
| <a name="input_webhook_path"></a> [webhook\_path](#input\_webhook\_path) | Webhook path | `string` | `"/webhook"` | no |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_slack_channel"></a> [slack\_channel](#output\_slack\_channel) | Slack channel messages are posted to |
| <a name="output_webhook_password"></a> [webhook\_password](#output\_webhook\_password) | Password for webhook authentication |
| <a name="output_webhook_url"></a> [webhook\_url](#output\_webhook\_url) | Webhook url |
| <a name="output_webhook_username"></a> [webhook\_username](#output\_webhook\_username) | Username for webhook authentication |
