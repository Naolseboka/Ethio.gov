package et.gov.ethgov.analytics.domain.metric;

import java.time.Instant;

public record MetricDefinitionCancelled(MetricDefinitionId metricDefinitionId, String reason, Instant occurredAt) implements MetricDefinitionDomainEvent {}
