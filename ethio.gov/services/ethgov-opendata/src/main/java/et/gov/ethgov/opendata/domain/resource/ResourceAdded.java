package et.gov.ethgov.opendata.domain.resource;

import et.gov.ethgov.opendata.domain.dataset.DatasetId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record ResourceAdded(
        ResourceId resourceId,
        TenantId tenantId,
        ResourceNumber resourceNumber,
        DatasetId datasetId,
        ResourceFormat format,
        Instant occurredAt)
        implements ResourceDomainEvent {}
