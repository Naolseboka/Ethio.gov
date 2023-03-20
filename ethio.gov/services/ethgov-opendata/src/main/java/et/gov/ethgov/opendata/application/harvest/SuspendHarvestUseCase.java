package et.gov.ethgov.opendata.application.harvest;

import et.gov.ethgov.opendata.domain.harvest.Harvest;
import et.gov.ethgov.opendata.domain.harvest.HarvestId;
import et.gov.ethgov.opendata.domain.harvest.HarvestRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendHarvestUseCase {
    private final HarvestRepository repository;
    private final Clock clock;

    public SuspendHarvestUseCase(HarvestRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HarvestId execute(SuspendHarvestCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HarvestId id = HarvestId.parse(command.harvestId());
        Harvest aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("harvest not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendHarvestCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String harvestId,
            String reason) {}
}
