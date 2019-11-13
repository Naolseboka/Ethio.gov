package et.gov.ethgov.developer.application.portaluser;

import et.gov.ethgov.developer.domain.portaluser.Portaluser;
import et.gov.ethgov.developer.domain.portaluser.PortaluserId;
import et.gov.ethgov.developer.domain.portaluser.PortaluserRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePortaluserUseCase {
    private final PortaluserRepository repository;
    private final Clock clock;

    public ActivatePortaluserUseCase(PortaluserRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PortaluserId execute(ActivatePortaluserCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PortaluserId id = PortaluserId.parse(command.portaluserId());
        Portaluser aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("portaluser not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePortaluserCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String portaluserId,
            String reason) {}
}
