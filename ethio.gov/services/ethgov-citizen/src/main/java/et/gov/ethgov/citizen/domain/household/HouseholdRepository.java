package et.gov.ethgov.citizen.domain.household;

import java.util.List;
import java.util.Optional;

public interface HouseholdRepository {
    Household save(Household aggregate);

    Optional<Household> findById(HouseholdId id);

    Optional<Household> findByReferenceCode(String tenantId, String referenceCode);

    List<Household> findByTenant(String tenantId, int limit);

    void delete(HouseholdId id);
}
