package et.gov.ethgov.permits.application.occupancy;

import et.gov.ethgov.permits.domain.occupancy.Occupancy;
import et.gov.ethgov.permits.domain.occupancy.OccupancyId;
import et.gov.ethgov.permits.domain.occupancy.OccupancyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendOccupancyUseCase {
    private final OccupancyRepository repository;
    private final Clock clock;

    public SuspendOccupancyUseCase(OccupancyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OccupancyId execute(SuspendOccupancyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OccupancyId id = OccupancyId.parse(command.occupancyId());
        Occupancy aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("occupancy not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendOccupancyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String occupancyId,
            String reason) {}
}
