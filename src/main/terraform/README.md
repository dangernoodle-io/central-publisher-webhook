# Central Publisher Webhook Terraform Module

```tf
module "webhook" {
  source = "git@github.com:dangernoodle-io/central-publisher-webhook.git//src/main/terraform??ref=X.Y.Z"

}
```

## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | ~> 5 |
| <a name="requirement_maven"></a> [maven](#requirement\_maven) | ~> 0.2.1 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_aws"></a> [aws](#provider\_aws) | ~> 5 |
| <a name="provider_maven"></a> [maven](#provider\_maven) | ~> 0.2.1 |
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
| <a name="input_credentials"></a> [credentials](#input\_credentials) | Credentials for basic auth | <pre>object({<br/>    password   = optional(string, null)<br/>    pwd_length = optional(number, 16)<br/>    username   = string<br/>  })</pre> | <pre>{<br/>  "username": "central-publisher"<br/>}</pre> | no |
| <a name="input_fqdn"></a> [fqdn](#input\_fqdn) | n/a | <pre>object({<br/>    domain    = optional(string, null)<br/>    subdomain = optional(string, "central-publisher")<br/>  })</pre> | `{}` | no |
| <a name="input_log_retention"></a> [log\_retention](#input\_log\_retention) | n/a | <pre>object({<br/>    gateway = number<br/>    lambda  = number<br/>  })</pre> | <pre>{<br/>  "gateway": 14,<br/>  "lambda": 14<br/>}</pre> | no |
| <a name="input_name"></a> [name](#input\_name) | n/a | `string` | n/a | yes |
| <a name="input_ps_ext_arn"></a> [ps\_ext\_arn](#input\_ps\_ext\_arn) | Parameters and secrets lambda extension arn | `string` | n/a | yes |
| <a name="input_publisher"></a> [publisher](#input\_publisher) | n/a | <pre>object({<br/>    path    = optional(string, null)<br/>    version = string<br/>  })</pre> | n/a | yes |
| <a name="input_slack"></a> [slack](#input\_slack) | Slack configuration | <pre>object({<br/>    channel = string<br/>    token   = string<br/>  })</pre> | n/a | yes |
| <a name="input_ssm_prefix"></a> [ssm\_prefix](#input\_ssm\_prefix) | Parameter store prefix (must start with `/` | `string` | n/a | yes |
| <a name="input_timezone"></a> [timezone](#input\_timezone) | Timezone for lambda execution environment | `string` | `"America/Denver"` | no |
| <a name="input_webhook_path"></a> [webhook\_path](#input\_webhook\_path) | Webhook path | `string` | `"/webhook"` | no |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_central_password"></a> [central\_password](#output\_central\_password) | n/a |
| <a name="output_central_username"></a> [central\_username](#output\_central\_username) | n/a |
| <a name="output_slack_channel"></a> [slack\_channel](#output\_slack\_channel) | n/a |
| <a name="output_webhook_url"></a> [webhook\_url](#output\_webhook\_url) | n/a |
