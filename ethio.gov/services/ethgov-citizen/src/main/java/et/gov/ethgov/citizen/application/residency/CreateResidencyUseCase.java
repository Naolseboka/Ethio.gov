package et.gov.ethgov.citizen.application.residency;

import et.gov.ethgov.citizen.domain.residency.Residency;
import et.gov.ethgov.citizen.domain.residency.ResidencyId;
import et.gov.ethgov.citizen.domain.residency.ResidencyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateResidencyUseCase {
    private final ResidencyRepository repository;
    private final Clock clock;

    public CreateResidencyUseCase(ResidencyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ResidencyId execute(CreateResidencyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Residency aggregate = Residency.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateResidencyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String residencyId,
            String reason) {}
}
