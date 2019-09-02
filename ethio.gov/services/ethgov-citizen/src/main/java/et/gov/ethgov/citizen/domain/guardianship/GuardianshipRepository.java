package et.gov.ethgov.citizen.domain.guardianship;

import java.util.List;
import java.util.Optional;

public interface GuardianshipRepository {
    Guardianship save(Guardianship aggregate);

    Optional<Guardianship> findById(GuardianshipId id);

    Optional<Guardianship> findByReferenceCode(String tenantId, String referenceCode);

    List<Guardianship> findByTenant(String tenantId, int limit);

    void delete(GuardianshipId id);
}
