package et.gov.ethgov.analytics.domain.observation;

import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.math.BigDecimal;
import java.time.Instant;

public record ObservationRecorded(
        ObservationId observationId,
        TenantId tenantId,
        ObservationNumber observationNumber,
        MetricDefinitionId metricDefinitionId,
        BigDecimal value,
        Instant occurredAt)
        implements ObservationDomainEvent {}
