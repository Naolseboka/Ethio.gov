package et.gov.ethgov.citizen.application.guardianship;

import et.gov.ethgov.citizen.domain.guardianship.Guardianship;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipId;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateGuardianshipUseCase {
    private final GuardianshipRepository repository;
    private final Clock clock;

    public ActivateGuardianshipUseCase(GuardianshipRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GuardianshipId execute(ActivateGuardianshipCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        GuardianshipId id = GuardianshipId.parse(command.guardianshipId());
        Guardianship aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("guardianship not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateGuardianshipCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String guardianshipId,
            String reason) {}
}
