package et.gov.ethgov.reporting.domain.run;

import java.time.Instant;

public record ReportRunFailed(ReportRunId reportRunId, String reason, Instant occurredAt) implements ReportRunDomainEvent {}
