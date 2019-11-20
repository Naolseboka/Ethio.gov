package et.gov.ethgov.developer.application.sandbox;

import et.gov.ethgov.developer.domain.sandbox.Sandbox;
import et.gov.ethgov.developer.domain.sandbox.SandboxId;
import et.gov.ethgov.developer.domain.sandbox.SandboxRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSandboxUseCase {
    private final SandboxRepository repository;
    private final Clock clock;

    public CreateSandboxUseCase(SandboxRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SandboxId execute(CreateSandboxCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Sandbox aggregate = Sandbox.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSandboxCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sandboxId,
            String reason) {}
}
