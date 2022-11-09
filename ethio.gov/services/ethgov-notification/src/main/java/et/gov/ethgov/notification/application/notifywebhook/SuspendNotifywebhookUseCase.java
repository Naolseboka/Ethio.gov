package et.gov.ethgov.notification.application.notifywebhook;

import et.gov.ethgov.notification.domain.notifywebhook.Notifywebhook;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookId;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendNotifywebhookUseCase {
    private final NotifywebhookRepository repository;
    private final Clock clock;

    public SuspendNotifywebhookUseCase(NotifywebhookRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public NotifywebhookId execute(SuspendNotifywebhookCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        NotifywebhookId id = NotifywebhookId.parse(command.notifywebhookId());
        Notifywebhook aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("notifywebhook not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendNotifywebhookCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String notifywebhookId,
            String reason) {}
}
