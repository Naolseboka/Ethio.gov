package et.gov.ethgov.opendata.application.portalpage;

import et.gov.ethgov.opendata.domain.portalpage.Portalpage;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageId;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePortalpageUseCase {
    private final PortalpageRepository repository;
    private final Clock clock;

    public UpdatePortalpageUseCase(PortalpageRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PortalpageId execute(UpdatePortalpageCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PortalpageId id = PortalpageId.parse(command.portalpageId());
        Portalpage aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("portalpage not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePortalpageCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String portalpageId,
            String reason) {}
}
