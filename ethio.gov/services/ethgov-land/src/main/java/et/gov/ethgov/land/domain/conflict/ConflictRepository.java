package et.gov.ethgov.land.domain.conflict;

import java.util.List;
import java.util.Optional;

public interface ConflictRepository {
    Conflict save(Conflict aggregate);

    Optional<Conflict> findById(ConflictId id);

    Optional<Conflict> findByReferenceCode(String tenantId, String referenceCode);

    List<Conflict> findByTenant(String tenantId, int limit);

    void delete(ConflictId id);
}
