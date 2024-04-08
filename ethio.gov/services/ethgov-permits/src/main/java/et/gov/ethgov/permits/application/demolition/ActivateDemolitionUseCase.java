package et.gov.ethgov.permits.application.demolition;

import et.gov.ethgov.permits.domain.demolition.Demolition;
import et.gov.ethgov.permits.domain.demolition.DemolitionId;
import et.gov.ethgov.permits.domain.demolition.DemolitionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDemolitionUseCase {
    private final DemolitionRepository repository;
    private final Clock clock;

    public ActivateDemolitionUseCase(DemolitionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DemolitionId execute(ActivateDemolitionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DemolitionId id = DemolitionId.parse(command.demolitionId());
        Demolition aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("demolition not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDemolitionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String demolitionId,
            String reason) {}
}
