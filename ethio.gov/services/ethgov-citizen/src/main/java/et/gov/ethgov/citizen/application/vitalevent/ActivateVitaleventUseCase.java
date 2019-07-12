package et.gov.ethgov.citizen.application.vitalevent;

import et.gov.ethgov.citizen.domain.vitalevent.Vitalevent;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventId;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateVitaleventUseCase {
    private final VitaleventRepository repository;
    private final Clock clock;

    public ActivateVitaleventUseCase(VitaleventRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public VitaleventId execute(ActivateVitaleventCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        VitaleventId id = VitaleventId.parse(command.vitaleventId());
        Vitalevent aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("vitalevent not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateVitaleventCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String vitaleventId,
            String reason) {}
}
