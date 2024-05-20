package et.gov.ethgov.permits.domain.amendment;

import java.util.List;
import java.util.Optional;

public interface AmendmentRepository {
    Amendment save(Amendment aggregate);

    Optional<Amendment> findById(AmendmentId id);

    Optional<Amendment> findByReferenceCode(String tenantId, String referenceCode);

    List<Amendment> findByTenant(String tenantId, int limit);

    void delete(AmendmentId id);
}
