package et.gov.ethgov.permits.domain.temporary;

import java.util.List;
import java.util.Optional;

public interface TemporaryRepository {
    Temporary save(Temporary aggregate);

    Optional<Temporary> findById(TemporaryId id);

    Optional<Temporary> findByReferenceCode(String tenantId, String referenceCode);

    List<Temporary> findByTenant(String tenantId, int limit);

    void delete(TemporaryId id);
}
