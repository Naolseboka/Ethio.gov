package et.gov.ethgov.citizen.domain.citizenship;

import java.util.List;
import java.util.Optional;

public interface CitizenshipRepository {
    Citizenship save(Citizenship aggregate);

    Optional<Citizenship> findById(CitizenshipId id);

    Optional<Citizenship> findByReferenceCode(String tenantId, String referenceCode);

    List<Citizenship> findByTenant(String tenantId, int limit);

    void delete(CitizenshipId id);
}
