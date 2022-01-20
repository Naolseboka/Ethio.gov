package et.gov.ethgov.land.application.boundary;

import et.gov.ethgov.land.domain.boundary.Boundary;
import et.gov.ethgov.land.domain.boundary.BoundaryId;
import et.gov.ethgov.land.domain.boundary.BoundaryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendBoundaryUseCase {
    private final BoundaryRepository repository;
    private final Clock clock;

    public SuspendBoundaryUseCase(BoundaryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BoundaryId execute(SuspendBoundaryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BoundaryId id = BoundaryId.parse(command.boundaryId());
        Boundary aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("boundary not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendBoundaryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String boundaryId,
            String reason) {}
}
