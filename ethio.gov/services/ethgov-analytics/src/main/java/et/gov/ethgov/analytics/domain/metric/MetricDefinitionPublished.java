package et.gov.ethgov.analytics.domain.metric;

import java.time.Instant;

public record MetricDefinitionPublished(MetricDefinitionId metricDefinitionId, Instant occurredAt) implements MetricDefinitionDomainEvent {}
