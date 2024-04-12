package et.gov.ethgov.permits.application.occupancy;

import et.gov.ethgov.permits.domain.occupancy.Occupancy;
import et.gov.ethgov.permits.domain.occupancy.OccupancyId;
import et.gov.ethgov.permits.domain.occupancy.OccupancyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOccupancyUseCase {
    private final OccupancyRepository repository;
    private final Clock clock;

    public CreateOccupancyUseCase(OccupancyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OccupancyId execute(CreateOccupancyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Occupancy aggregate = Occupancy.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOccupancyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String occupancyId,
            String reason) {}
}
