package et.gov.ethgov.developer.application.sandbox;

import et.gov.ethgov.developer.domain.sandbox.Sandbox;
import et.gov.ethgov.developer.domain.sandbox.SandboxId;
import et.gov.ethgov.developer.domain.sandbox.SandboxRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSandboxUseCase {
    private final SandboxRepository repository;
    private final Clock clock;

    public ActivateSandboxUseCase(SandboxRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SandboxId execute(ActivateSandboxCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SandboxId id = SandboxId.parse(command.sandboxId());
        Sandbox aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("sandbox not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSandboxCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sandboxId,
            String reason) {}
}
