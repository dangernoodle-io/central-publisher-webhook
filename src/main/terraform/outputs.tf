output "slack_channel" {
  description = "Slack channel messages are posted to"
  value       = var.slack.channel
}

output "webhook_password" {
  description = "Password for webhook authentication"
  sensitive   = true
  value       = local.has_pwd ? var.credentials.password : random_password.password[0].result
}

output "webhook_url" {
  description = "Webhook url"
  value       = "https://${local.has_domain ? aws_route53_record.publisher[0].name : aws_apigatewayv2_api.publisher.api_endpoint}${var.webhook_path}"
}

output "webhook_username" {
  description = "Username for webhook authentication"
  value       = var.credentials.username
}
