package et.gov.ethgov.analytics.domain.metric;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record MetricDefinitionRegistered(
        MetricDefinitionId metricDefinitionId,
        TenantId tenantId,
        MetricNumber metricNumber,
        MetricCode code,
        MetricCategory category,
        Instant occurredAt)
        implements MetricDefinitionDomainEvent {}
