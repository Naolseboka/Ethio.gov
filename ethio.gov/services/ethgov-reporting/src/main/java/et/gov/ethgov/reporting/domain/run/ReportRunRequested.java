package et.gov.ethgov.reporting.domain.run;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionId;
import java.time.Instant;

public record ReportRunRequested(
        ReportRunId reportRunId,
        TenantId tenantId,
        RunNumber runNumber,
        ReportDefinitionId reportDefinitionId,
        Instant occurredAt)
        implements ReportRunDomainEvent {}
