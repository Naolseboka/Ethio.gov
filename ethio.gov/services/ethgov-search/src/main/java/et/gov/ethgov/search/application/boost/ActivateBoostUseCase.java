package et.gov.ethgov.search.application.boost;

import et.gov.ethgov.search.domain.boost.Boost;
import et.gov.ethgov.search.domain.boost.BoostId;
import et.gov.ethgov.search.domain.boost.BoostRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateBoostUseCase {
    private final BoostRepository repository;
    private final Clock clock;

    public ActivateBoostUseCase(BoostRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BoostId execute(ActivateBoostCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BoostId id = BoostId.parse(command.boostId());
        Boost aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("boost not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateBoostCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String boostId,
            String reason) {}
}
