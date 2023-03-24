package et.gov.ethgov.opendata.application.portalpage;

import et.gov.ethgov.opendata.domain.portalpage.Portalpage;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageId;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePortalpageUseCase {
    private final PortalpageRepository repository;
    private final Clock clock;

    public CreatePortalpageUseCase(PortalpageRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PortalpageId execute(CreatePortalpageCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Portalpage aggregate = Portalpage.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePortalpageCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String portalpageId,
            String reason) {}
}
