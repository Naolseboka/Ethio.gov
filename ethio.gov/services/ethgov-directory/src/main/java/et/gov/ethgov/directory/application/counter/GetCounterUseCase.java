package et.gov.ethgov.directory.application.counter;

import et.gov.ethgov.directory.domain.counter.Counter;
import et.gov.ethgov.directory.domain.counter.CounterId;
import et.gov.ethgov.directory.domain.counter.CounterRepository;
import java.util.Objects;

public final class GetCounterUseCase {
    private final CounterRepository repository;

    public GetCounterUseCase(CounterRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Counter execute(String rawId) {
        CounterId id = CounterId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("counter not found: " + rawId));
    }
}
