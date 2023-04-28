package et.gov.ethgov.opendata.domain.dataset;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface DatasetRepository {
    boolean existsByTenantAndSlug(TenantId tenantId, DatasetSlug slug);

    Optional<OpenDataset> findById(DatasetId id);

    Optional<OpenDataset> findByTenantAndDatasetNumber(TenantId tenantId, DatasetNumber datasetNumber);

    Optional<OpenDataset> findByTenantAndSlug(TenantId tenantId, DatasetSlug slug);

    void save(OpenDataset dataset);
}
