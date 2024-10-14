package et.gov.ethgov.reporting.domain.definition;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record ReportDefinitionRegistered(ReportDefinitionId reportDefinitionId, TenantId tenantId, ReportNumber reportNumber, ReportCode code, ReportCategory category, Instant occurredAt) implements ReportDefinitionDomainEvent {}
