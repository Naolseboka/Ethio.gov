package et.gov.ethgov.developer.application.devwebhook;

import et.gov.ethgov.developer.domain.devwebhook.Devwebhook;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookId;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDevwebhookUseCase {
    private final DevwebhookRepository repository;
    private final Clock clock;

    public CreateDevwebhookUseCase(DevwebhookRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevwebhookId execute(CreateDevwebhookCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Devwebhook aggregate = Devwebhook.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDevwebhookCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devwebhookId,
            String reason) {}
}
