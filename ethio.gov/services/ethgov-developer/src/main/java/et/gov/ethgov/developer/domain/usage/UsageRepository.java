package et.gov.ethgov.developer.domain.usage;

import java.util.List;
import java.util.Optional;

public interface UsageRepository {
    Usage save(Usage aggregate);

    Optional<Usage> findById(UsageId id);

    Optional<Usage> findByReferenceCode(String tenantId, String referenceCode);

    List<Usage> findByTenant(String tenantId, int limit);

    void delete(UsageId id);
}
