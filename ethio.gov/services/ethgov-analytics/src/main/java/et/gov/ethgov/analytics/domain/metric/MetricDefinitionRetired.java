package et.gov.ethgov.analytics.domain.metric;

import java.time.Instant;

public record MetricDefinitionRetired(MetricDefinitionId metricDefinitionId, Instant occurredAt) implements MetricDefinitionDomainEvent {}
