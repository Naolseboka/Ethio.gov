package et.gov.ethgov.reporting.domain.run;

import java.time.Instant;

public record ReportRunSucceeded(ReportRunId reportRunId, String artifactReference, Instant occurredAt) implements ReportRunDomainEvent {}
