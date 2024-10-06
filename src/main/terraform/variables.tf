variable "credentials" {
  default = {
    username = "central-publisher"
  }
  description = "Credentials for basic auth"
  type = object({
    password   = optional(string, null)
    pwd_length = optional(number, 16)
    username   = string
  })
}

variable "fqdn" {
  default = {}
  type = object({
    domain    = optional(string, null)
    subdomain = optional(string, "central-publisher")
  })
}

variable "log_retention" {
  default = {
    gateway = 14
    lambda  = 14
  }
  type = object({
    gateway = number
    lambda  = number
  })
}

variable "name" {
  type = string
}

variable "ps_ext_arn" {
  description = "Parameters and secrets lambda extension arn"
  type        = string
}

variable "publisher" {
  type = object({
    path    = optional(string, null)
    version = string
  })
}

variable "slack" {
  description = "Slack configuration"
  type = object({
    channel = string
    token   = string
  })
}

variable "ssm_prefix" {
  description = "Parameter store prefix (must start with `/`"
  type        = string
}

variable "timezone" {
  default     = "America/Denver"
  description = "Timezone for lambda execution environment"
  type        = string
}

variable "webhook_path" {
  default     = "/webhook"
  description = "Webhook path"
  type        = string
}
