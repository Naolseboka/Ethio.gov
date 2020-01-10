package et.gov.ethgov.developer.domain.devwebhook;

import java.util.List;
import java.util.Optional;

public interface DevwebhookRepository {
    Devwebhook save(Devwebhook aggregate);

    Optional<Devwebhook> findById(DevwebhookId id);

    Optional<Devwebhook> findByReferenceCode(String tenantId, String referenceCode);

    List<Devwebhook> findByTenant(String tenantId, int limit);

    void delete(DevwebhookId id);
}
