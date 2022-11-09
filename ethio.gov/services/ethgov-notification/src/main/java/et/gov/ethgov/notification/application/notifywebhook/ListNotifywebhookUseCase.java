package et.gov.ethgov.notification.application.notifywebhook;

import et.gov.ethgov.notification.domain.notifywebhook.Notifywebhook;
import et.gov.ethgov.notification.domain.notifywebhook.NotifywebhookRepository;
import java.util.List;
import java.util.Objects;

public final class ListNotifywebhookUseCase {
    private final NotifywebhookRepository repository;

    public ListNotifywebhookUseCase(NotifywebhookRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Notifywebhook> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
