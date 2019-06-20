package et.gov.ethgov.citizen.application.disabilitystatus;

import et.gov.ethgov.citizen.domain.disabilitystatus.Disabilitystatus;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusId;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDisabilitystatusUseCase {
    private final DisabilitystatusRepository repository;
    private final Clock clock;

    public CreateDisabilitystatusUseCase(DisabilitystatusRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DisabilitystatusId execute(CreateDisabilitystatusCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Disabilitystatus aggregate = Disabilitystatus.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDisabilitystatusCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String disabilitystatusId,
            String reason) {}
}
