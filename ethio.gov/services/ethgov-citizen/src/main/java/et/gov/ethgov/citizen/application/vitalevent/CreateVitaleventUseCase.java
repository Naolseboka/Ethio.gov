package et.gov.ethgov.citizen.application.vitalevent;

import et.gov.ethgov.citizen.domain.vitalevent.Vitalevent;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventId;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateVitaleventUseCase {
    private final VitaleventRepository repository;
    private final Clock clock;

    public CreateVitaleventUseCase(VitaleventRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public VitaleventId execute(CreateVitaleventCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Vitalevent aggregate = Vitalevent.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateVitaleventCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String vitaleventId,
            String reason) {}
}
