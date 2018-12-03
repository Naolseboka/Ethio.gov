package et.gov.ethgov.analytics.domain.metric;

import java.time.Instant;

public record MetricDefinitionUpdated(MetricDefinitionId metricDefinitionId, Instant occurredAt) implements MetricDefinitionDomainEvent {}
