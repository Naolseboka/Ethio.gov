package et.gov.ethgov.reporting.domain.definition;

import java.time.Instant;

public sealed interface ReportDefinitionDomainEvent
        permits ReportDefinitionRegistered,
                ReportDefinitionUpdated,
                ReportDefinitionPublished,
                ReportDefinitionRetired,
                ReportDefinitionCancelled {
    ReportDefinitionId reportDefinitionId();

    Instant occurredAt();
}
