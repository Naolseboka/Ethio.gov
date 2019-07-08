package et.gov.ethgov.citizen.application.household;

import et.gov.ethgov.citizen.domain.household.Household;
import et.gov.ethgov.citizen.domain.household.HouseholdRepository;
import java.util.List;
import java.util.Objects;

public final class ListHouseholdUseCase {
    private final HouseholdRepository repository;

    public ListHouseholdUseCase(HouseholdRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Household> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
