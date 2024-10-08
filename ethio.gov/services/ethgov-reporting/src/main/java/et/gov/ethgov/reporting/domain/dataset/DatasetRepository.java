package et.gov.ethgov.reporting.domain.dataset;

import java.util.List;
import java.util.Optional;

public interface DatasetRepository {
    Dataset save(Dataset aggregate);

    Optional<Dataset> findById(DatasetId id);

    Optional<Dataset> findByReferenceCode(String tenantId, String referenceCode);

    List<Dataset> findByTenant(String tenantId, int limit);

    void delete(DatasetId id);
}
