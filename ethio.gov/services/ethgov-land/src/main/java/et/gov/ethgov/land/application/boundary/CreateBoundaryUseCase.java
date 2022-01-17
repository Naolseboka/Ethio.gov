package et.gov.ethgov.land.application.boundary;

import et.gov.ethgov.land.domain.boundary.Boundary;
import et.gov.ethgov.land.domain.boundary.BoundaryId;
import et.gov.ethgov.land.domain.boundary.BoundaryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateBoundaryUseCase {
    private final BoundaryRepository repository;
    private final Clock clock;

    public CreateBoundaryUseCase(BoundaryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BoundaryId execute(CreateBoundaryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Boundary aggregate = Boundary.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateBoundaryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String boundaryId,
            String reason) {}
}
