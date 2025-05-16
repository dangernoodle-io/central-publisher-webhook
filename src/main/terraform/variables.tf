variable "artifact" {
  description = "Jar artifact"
  type = object({
    path    = optional(string, null)
    version = string
  })
}

variable "credentials" {
  default     = {}
  description = "Credentials for basic auth"
  type = object({
    password   = optional(string, null)
    pwd_length = optional(number, 16)
    username   = optional(string, "central-publisher")
  })
}

variable "fqdn" {
  default     = {}
  description = "Custom webhook domain"
  type = object({
    domain    = optional(string, null)
    subdomain = optional(string, "central-publisher")
  })
}

variable "log_retention" {
  default     = {}
  description = "Log retention (in days)"
  type = object({
    gateway = optional(number, 14)
    lambda  = optional(number, 14)
  })
}

variable "name" {
  default     = "central-publisher-webhook"
  description = "Lambda / Role name"
  type        = string
}

variable "ps_ext_arn" {
  description = "Parameters and secrets lambda extension arn"
  type        = string
}

variable "slack" {
  description = "Slack configuration"
  type = object({
    channel = string
    token   = string
  })
}

variable "pstore_prefix" {
  default     = "central-publisher-webhook"
  description = "Parameter store prefix"
  type        = string
}

variable "timezone" {
  description = "Timezone for lambda execution environment (eg: America/Denver)"
  type        = string
}

variable "webhook_path" {
  default     = "/webhook"
  description = "Webhook path"
  type        = string
}
