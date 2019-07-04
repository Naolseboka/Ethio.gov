package et.gov.ethgov.citizen.application.household;

import et.gov.ethgov.citizen.domain.household.Household;
import et.gov.ethgov.citizen.domain.household.HouseholdId;
import et.gov.ethgov.citizen.domain.household.HouseholdRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateHouseholdUseCase {
    private final HouseholdRepository repository;
    private final Clock clock;

    public ActivateHouseholdUseCase(HouseholdRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HouseholdId execute(ActivateHouseholdCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HouseholdId id = HouseholdId.parse(command.householdId());
        Household aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("household not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateHouseholdCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String householdId,
            String reason) {}
}
