package et.gov.ethgov.document.domain.retention;

import java.util.List;
import java.util.Optional;

public interface RetentionRepository {
    Retention save(Retention aggregate);

    Optional<Retention> findById(RetentionId id);

    Optional<Retention> findByReferenceCode(String tenantId, String referenceCode);

    List<Retention> findByTenant(String tenantId, int limit);

    void delete(RetentionId id);
}
