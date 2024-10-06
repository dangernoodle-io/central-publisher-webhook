output "central_password" {
  sensitive = true
  value     = local.has_pwd ? var.credentials.password : random_password.password[0].result
}

output "central_username" {
  value = var.credentials.username
}

output "slack_channel" {
  value = var.slack.channel
}

output "webhook_url" {
  value = "https://${local.has_domain ? aws_route53_record.publisher[0].name : aws_apigatewayv2_api.publisher.api_endpoint}/${var.webhook_path}}"
}
