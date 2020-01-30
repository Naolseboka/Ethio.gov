package et.gov.ethgov.developer.domain.sandbox;

import java.util.List;
import java.util.Optional;

public interface SandboxRepository {
    Sandbox save(Sandbox aggregate);

    Optional<Sandbox> findById(SandboxId id);

    Optional<Sandbox> findByReferenceCode(String tenantId, String referenceCode);

    List<Sandbox> findByTenant(String tenantId, int limit);

    void delete(SandboxId id);
}
