package et.gov.ethgov.land.application.boundary;

import et.gov.ethgov.land.domain.boundary.Boundary;
import et.gov.ethgov.land.domain.boundary.BoundaryId;
import et.gov.ethgov.land.domain.boundary.BoundaryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateBoundaryUseCase {
    private final BoundaryRepository repository;
    private final Clock clock;

    public UpdateBoundaryUseCase(BoundaryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BoundaryId execute(UpdateBoundaryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BoundaryId id = BoundaryId.parse(command.boundaryId());
        Boundary aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("boundary not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateBoundaryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String boundaryId,
            String reason) {}
}
