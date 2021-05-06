package et.gov.ethgov.gis.application.boundaryset;

import et.gov.ethgov.gis.domain.boundaryset.Boundaryset;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetId;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateBoundarysetUseCase {
    private final BoundarysetRepository repository;
    private final Clock clock;

    public UpdateBoundarysetUseCase(BoundarysetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BoundarysetId execute(UpdateBoundarysetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BoundarysetId id = BoundarysetId.parse(command.boundarysetId());
        Boundaryset aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("boundaryset not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateBoundarysetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String boundarysetId,
            String reason) {}
}
