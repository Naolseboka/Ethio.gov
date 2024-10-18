package et.gov.ethgov.reporting.domain.definition;

import java.time.Instant;

public record ReportDefinitionUpdated(ReportDefinitionId reportDefinitionId, Instant occurredAt) implements ReportDefinitionDomainEvent {}
