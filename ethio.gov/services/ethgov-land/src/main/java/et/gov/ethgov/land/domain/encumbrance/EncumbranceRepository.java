package et.gov.ethgov.land.domain.encumbrance;

import java.util.List;
import java.util.Optional;

public interface EncumbranceRepository {
    Encumbrance save(Encumbrance aggregate);

    Optional<Encumbrance> findById(EncumbranceId id);

    Optional<Encumbrance> findByReferenceCode(String tenantId, String referenceCode);

    List<Encumbrance> findByTenant(String tenantId, int limit);

    void delete(EncumbranceId id);
}
