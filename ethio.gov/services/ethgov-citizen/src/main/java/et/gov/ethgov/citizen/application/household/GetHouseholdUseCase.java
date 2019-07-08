package et.gov.ethgov.citizen.application.household;

import et.gov.ethgov.citizen.domain.household.Household;
import et.gov.ethgov.citizen.domain.household.HouseholdId;
import et.gov.ethgov.citizen.domain.household.HouseholdRepository;
import java.util.Objects;

public final class GetHouseholdUseCase {
    private final HouseholdRepository repository;

    public GetHouseholdUseCase(HouseholdRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Household execute(String rawId) {
        HouseholdId id = HouseholdId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("household not found: " + rawId));
    }
}
