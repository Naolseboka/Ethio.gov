package et.gov.ethgov.document.domain.classification;

import java.util.List;
import java.util.Optional;

public interface ClassificationRepository {
    Classification save(Classification aggregate);

    Optional<Classification> findById(ClassificationId id);

    Optional<Classification> findByReferenceCode(String tenantId, String referenceCode);

    List<Classification> findByTenant(String tenantId, int limit);

    void delete(ClassificationId id);
}
