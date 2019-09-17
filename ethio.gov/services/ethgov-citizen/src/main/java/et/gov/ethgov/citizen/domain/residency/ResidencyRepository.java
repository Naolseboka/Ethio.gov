package et.gov.ethgov.citizen.domain.residency;

import java.util.List;
import java.util.Optional;

public interface ResidencyRepository {
    Residency save(Residency aggregate);

    Optional<Residency> findById(ResidencyId id);

    Optional<Residency> findByReferenceCode(String tenantId, String referenceCode);

    List<Residency> findByTenant(String tenantId, int limit);

    void delete(ResidencyId id);
}
