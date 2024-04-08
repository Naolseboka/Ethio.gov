package et.gov.ethgov.permits.application.demolition;

import et.gov.ethgov.permits.domain.demolition.Demolition;
import et.gov.ethgov.permits.domain.demolition.DemolitionId;
import et.gov.ethgov.permits.domain.demolition.DemolitionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDemolitionUseCase {
    private final DemolitionRepository repository;
    private final Clock clock;

    public CreateDemolitionUseCase(DemolitionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DemolitionId execute(CreateDemolitionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Demolition aggregate = Demolition.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDemolitionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String demolitionId,
            String reason) {}
}
