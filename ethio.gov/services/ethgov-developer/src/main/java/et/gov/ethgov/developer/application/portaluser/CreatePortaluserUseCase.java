package et.gov.ethgov.developer.application.portaluser;

import et.gov.ethgov.developer.domain.portaluser.Portaluser;
import et.gov.ethgov.developer.domain.portaluser.PortaluserId;
import et.gov.ethgov.developer.domain.portaluser.PortaluserRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePortaluserUseCase {
    private final PortaluserRepository repository;
    private final Clock clock;

    public CreatePortaluserUseCase(PortaluserRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PortaluserId execute(CreatePortaluserCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Portaluser aggregate = Portaluser.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePortaluserCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String portaluserId,
            String reason) {}
}
