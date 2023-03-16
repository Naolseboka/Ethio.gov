package et.gov.ethgov.opendata.application.harvest;

import et.gov.ethgov.opendata.domain.harvest.Harvest;
import et.gov.ethgov.opendata.domain.harvest.HarvestId;
import et.gov.ethgov.opendata.domain.harvest.HarvestRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateHarvestUseCase {
    private final HarvestRepository repository;
    private final Clock clock;

    public CreateHarvestUseCase(HarvestRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HarvestId execute(CreateHarvestCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Harvest aggregate = Harvest.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateHarvestCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String harvestId,
            String reason) {}
}
