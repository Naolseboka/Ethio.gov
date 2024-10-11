package et.gov.ethgov.reporting.domain.definition;

import java.time.Instant;

public record ReportDefinitionCancelled(ReportDefinitionId reportDefinitionId, String reason, Instant occurredAt) implements ReportDefinitionDomainEvent {}
