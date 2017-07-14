variable "name" {
  type        = string
  description = "PostgreSQL instance identifier"
}

variable "database_name" {
  type        = string
  description = "Logical database name"
  default     = "ethgov"
}

variable "username" {
  type        = string
  description = "Application database username"
  default     = "ethgov"
}

# Phase 0 defines the module contract. Concrete cloud resources are added when
# the first environment (non-local) is provisioned. Local development uses Docker Compose.
output "connection_contract" {
  value = {
    engine         = "postgres"
    version        = "16"
    extensions     = ["postgis"]
    database_name  = var.database_name
    username       = var.username
    ssl_mode       = "require"
    name           = var.name
  }
}
