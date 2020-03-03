package et.gov.ethgov.directory.application.counter;

import et.gov.ethgov.directory.domain.counter.Counter;
import et.gov.ethgov.directory.domain.counter.CounterId;
import et.gov.ethgov.directory.domain.counter.CounterRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCounterUseCase {
    private final CounterRepository repository;
    private final Clock clock;

    public ActivateCounterUseCase(CounterRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CounterId execute(ActivateCounterCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CounterId id = CounterId.parse(command.counterId());
        Counter aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("counter not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCounterCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String counterId,
            String reason) {}
}
