package et.gov.ethgov.citizen.application.household;

import et.gov.ethgov.citizen.domain.household.Household;
import et.gov.ethgov.citizen.domain.household.HouseholdId;
import et.gov.ethgov.citizen.domain.household.HouseholdRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendHouseholdUseCase {
    private final HouseholdRepository repository;
    private final Clock clock;

    public SuspendHouseholdUseCase(HouseholdRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HouseholdId execute(SuspendHouseholdCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HouseholdId id = HouseholdId.parse(command.householdId());
        Household aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("household not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendHouseholdCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String householdId,
            String reason) {}
}
