package et.gov.ethgov.gis.application.boundaryset;

import et.gov.ethgov.gis.domain.boundaryset.Boundaryset;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetId;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateBoundarysetUseCase {
    private final BoundarysetRepository repository;
    private final Clock clock;

    public CreateBoundarysetUseCase(BoundarysetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BoundarysetId execute(CreateBoundarysetCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Boundaryset aggregate = Boundaryset.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateBoundarysetCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String boundarysetId,
            String reason) {}
}
