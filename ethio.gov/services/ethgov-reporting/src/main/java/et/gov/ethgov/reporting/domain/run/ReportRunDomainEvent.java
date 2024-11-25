package et.gov.ethgov.reporting.domain.run;

import java.time.Instant;

public sealed interface ReportRunDomainEvent
        permits ReportRunRequested, ReportRunStarted, ReportRunSucceeded, ReportRunFailed, ReportRunCancelled {
    ReportRunId reportRunId();

    Instant occurredAt();
}
