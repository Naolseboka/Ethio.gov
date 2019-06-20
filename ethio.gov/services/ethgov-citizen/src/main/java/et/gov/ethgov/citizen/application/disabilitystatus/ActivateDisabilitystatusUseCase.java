package et.gov.ethgov.citizen.application.disabilitystatus;

import et.gov.ethgov.citizen.domain.disabilitystatus.Disabilitystatus;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusId;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDisabilitystatusUseCase {
    private final DisabilitystatusRepository repository;
    private final Clock clock;

    public ActivateDisabilitystatusUseCase(DisabilitystatusRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DisabilitystatusId execute(ActivateDisabilitystatusCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DisabilitystatusId id = DisabilitystatusId.parse(command.disabilitystatusId());
        Disabilitystatus aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("disabilitystatus not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDisabilitystatusCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String disabilitystatusId,
            String reason) {}
}
