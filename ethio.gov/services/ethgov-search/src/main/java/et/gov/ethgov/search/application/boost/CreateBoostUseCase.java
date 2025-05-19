package et.gov.ethgov.search.application.boost;

import et.gov.ethgov.search.domain.boost.Boost;
import et.gov.ethgov.search.domain.boost.BoostId;
import et.gov.ethgov.search.domain.boost.BoostRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateBoostUseCase {
    private final BoostRepository repository;
    private final Clock clock;

    public CreateBoostUseCase(BoostRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BoostId execute(CreateBoostCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Boost aggregate = Boost.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateBoostCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String boostId,
            String reason) {}
}
