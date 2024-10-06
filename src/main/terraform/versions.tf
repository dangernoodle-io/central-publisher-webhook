terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5"
    }
    maven = {
      source  = "kota65535/maven"
      version = "~> 0.2.1"
    }
  }
}
