package et.gov.ethgov.reporting.domain.run;

import java.time.Instant;

public record ReportRunStarted(ReportRunId reportRunId, Instant occurredAt) implements ReportRunDomainEvent {}
