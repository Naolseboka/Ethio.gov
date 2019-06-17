package et.gov.ethgov.citizen.application.citizenship;

import et.gov.ethgov.citizen.domain.citizenship.Citizenship;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipId;
import et.gov.ethgov.citizen.domain.citizenship.CitizenshipRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCitizenshipUseCase {
    private final CitizenshipRepository repository;
    private final Clock clock;

    public CreateCitizenshipUseCase(CitizenshipRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CitizenshipId execute(CreateCitizenshipCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Citizenship aggregate = Citizenship.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCitizenshipCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String citizenshipId,
            String reason) {}
}
