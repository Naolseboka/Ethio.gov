package et.gov.ethgov.analytics.domain.metric;

import java.time.Instant;

public sealed interface MetricDefinitionDomainEvent
        permits MetricDefinitionRegistered,
                MetricDefinitionUpdated,
                MetricDefinitionPublished,
                MetricDefinitionRetired,
                MetricDefinitionCancelled {
    MetricDefinitionId metricDefinitionId();

    Instant occurredAt();
}
