package et.gov.ethgov.citizen.application.disabilitystatus;

import et.gov.ethgov.citizen.domain.disabilitystatus.Disabilitystatus;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusId;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDisabilitystatusUseCase {
    private final DisabilitystatusRepository repository;
    private final Clock clock;

    public UpdateDisabilitystatusUseCase(DisabilitystatusRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DisabilitystatusId execute(UpdateDisabilitystatusCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DisabilitystatusId id = DisabilitystatusId.parse(command.disabilitystatusId());
        Disabilitystatus aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("disabilitystatus not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDisabilitystatusCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String disabilitystatusId,
            String reason) {}
}
