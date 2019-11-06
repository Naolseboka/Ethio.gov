package et.gov.ethgov.developer.application.devratelimit;

import et.gov.ethgov.developer.domain.devratelimit.Devratelimit;
import et.gov.ethgov.developer.domain.devratelimit.DevratelimitId;
import et.gov.ethgov.developer.domain.devratelimit.DevratelimitRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDevratelimitUseCase {
    private final DevratelimitRepository repository;
    private final Clock clock;

    public UpdateDevratelimitUseCase(DevratelimitRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevratelimitId execute(UpdateDevratelimitCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DevratelimitId id = DevratelimitId.parse(command.devratelimitId());
        Devratelimit aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devratelimit not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDevratelimitCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devratelimitId,
            String reason) {}
}
