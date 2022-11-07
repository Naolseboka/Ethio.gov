package et.gov.ethgov.notification.application.notifywebhook;

import et.gov.ethgov.notification.domain.notifywebhook.Notifywebhook;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookId;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookRepository;
import java.util.Objects;

public final class GetNotifywebhookUseCase {
    private final NotifywebhookRepository repository;

    public GetNotifywebhookUseCase(NotifywebhookRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Notifywebhook execute(String rawId) {
        NotifywebhookId id = NotifywebhookId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("notifywebhook not found: " + rawId));
    }
}
