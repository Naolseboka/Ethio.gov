package et.gov.ethgov.permits.application.demolition;

import et.gov.ethgov.permits.domain.demolition.Demolition;
import et.gov.ethgov.permits.domain.demolition.DemolitionId;
import et.gov.ethgov.permits.domain.demolition.DemolitionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendDemolitionUseCase {
    private final DemolitionRepository repository;
    private final Clock clock;

    public SuspendDemolitionUseCase(DemolitionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DemolitionId execute(SuspendDemolitionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DemolitionId id = DemolitionId.parse(command.demolitionId());
        Demolition aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("demolition not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendDemolitionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String demolitionId,
            String reason) {}
}
