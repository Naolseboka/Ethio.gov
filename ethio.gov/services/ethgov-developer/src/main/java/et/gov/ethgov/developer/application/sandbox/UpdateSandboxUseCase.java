package et.gov.ethgov.developer.application.sandbox;

import et.gov.ethgov.developer.domain.sandbox.Sandbox;
import et.gov.ethgov.developer.domain.sandbox.SandboxId;
import et.gov.ethgov.developer.domain.sandbox.SandboxRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSandboxUseCase {
    private final SandboxRepository repository;
    private final Clock clock;

    public UpdateSandboxUseCase(SandboxRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SandboxId execute(UpdateSandboxCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SandboxId id = SandboxId.parse(command.sandboxId());
        Sandbox aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("sandbox not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSandboxCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sandboxId,
            String reason) {}
}
