package et.gov.ethgov.directory.application.counter;

import et.gov.ethgov.directory.domain.counter.Counter;
import et.gov.ethgov.directory.domain.counter.CounterId;
import et.gov.ethgov.directory.domain.counter.CounterRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCounterUseCase {
    private final CounterRepository repository;
    private final Clock clock;

    public CreateCounterUseCase(CounterRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CounterId execute(CreateCounterCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Counter aggregate = Counter.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCounterCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String counterId,
            String reason) {}
}
