package et.gov.ethgov.permits.domain.extension;

import java.util.List;
import java.util.Optional;

public interface ExtensionRepository {
    Extension save(Extension aggregate);

    Optional<Extension> findById(ExtensionId id);

    Optional<Extension> findByReferenceCode(String tenantId, String referenceCode);

    List<Extension> findByTenant(String tenantId, int limit);

    void delete(ExtensionId id);
}
