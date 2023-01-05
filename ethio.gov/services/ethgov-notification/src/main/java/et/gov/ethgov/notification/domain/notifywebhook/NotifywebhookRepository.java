package et.gov.ethgov.notification.domain.notifywebhook;

import java.util.List;
import java.util.Optional;

public interface NotifywebhookRepository {
    Notifywebhook save(Notifywebhook aggregate);

    Optional<Notifywebhook> findById(NotifywebhookId id);

    Optional<Notifywebhook> findByReferenceCode(String tenantId, String referenceCode);

    List<Notifywebhook> findByTenant(String tenantId, int limit);

    void delete(NotifywebhookId id);
}
