package et.gov.ethgov.gateway.application.canary;

import et.gov.ethgov.gateway.domain.canary.Canary;
import et.gov.ethgov.gateway.domain.canary.CanaryId;
import et.gov.ethgov.gateway.domain.canary.CanaryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendCanaryUseCase {
    private final CanaryRepository repository;
    private final Clock clock;

    public SuspendCanaryUseCase(CanaryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CanaryId execute(SuspendCanaryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CanaryId id = CanaryId.parse(command.canaryId());
        Canary aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("canary not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendCanaryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String canaryId,
            String reason) {}
}
