package et.gov.ethgov.citizen.application.household;

import et.gov.ethgov.citizen.domain.household.Household;
import et.gov.ethgov.citizen.domain.household.HouseholdId;
import et.gov.ethgov.citizen.domain.household.HouseholdRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateHouseholdUseCase {
    private final HouseholdRepository repository;
    private final Clock clock;

    public CreateHouseholdUseCase(HouseholdRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HouseholdId execute(CreateHouseholdCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Household aggregate = Household.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateHouseholdCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String householdId,
            String reason) {}
}
