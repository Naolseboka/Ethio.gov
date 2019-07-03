package et.gov.ethgov.citizen.application.guardianship;

import et.gov.ethgov.citizen.domain.guardianship.Guardianship;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipId;
import et.gov.ethgov.citizen.domain.guardianship.GuardianshipRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateGuardianshipUseCase {
    private final GuardianshipRepository repository;
    private final Clock clock;

    public CreateGuardianshipUseCase(GuardianshipRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public GuardianshipId execute(CreateGuardianshipCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Guardianship aggregate = Guardianship.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateGuardianshipCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String guardianshipId,
            String reason) {}
}
