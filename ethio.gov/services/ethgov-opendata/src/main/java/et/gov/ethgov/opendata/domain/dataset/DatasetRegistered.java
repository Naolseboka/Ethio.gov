package et.gov.ethgov.opendata.domain.dataset;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record DatasetRegistered(
        DatasetId datasetId,
        TenantId tenantId,
        DatasetNumber datasetNumber,
        DatasetSlug slug,
        DatasetTheme theme,
        Instant occurredAt)
        implements DatasetDomainEvent {}
