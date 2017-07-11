terraform {
  required_version = ">= 1.7.0"
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.35"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.17"
    }
  }
}

variable "environment" {
  type        = string
  description = "Deployment environment name"
  default     = "local"
}

variable "namespace" {
  type        = string
  description = "Kubernetes namespace for EthGov platform components"
  default     = "ethgov"
}

resource "kubernetes_namespace" "ethgov" {
  metadata {
    name = var.namespace
    labels = {
      "app.kubernetes.io/part-of" = "ethgov"
      "ethgov.et/environment"     = var.environment
    }
  }
}

output "namespace" {
  value = kubernetes_namespace.ethgov.metadata[0].name
}
