package et.gov.ethgov.notification.application.notifywebhook;

import et.gov.ethgov.notification.domain.notifywebhook.Notifywebhook;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookId;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateNotifywebhookUseCase {
    private final NotifywebhookRepository repository;
    private final Clock clock;

    public CreateNotifywebhookUseCase(NotifywebhookRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public NotifywebhookId execute(CreateNotifywebhookCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Notifywebhook aggregate = Notifywebhook.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateNotifywebhookCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String notifywebhookId,
            String reason) {}
}
