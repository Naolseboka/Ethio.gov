package et.gov.ethgov.gateway.domain.auditlog;

import java.util.List;
import java.util.Optional;

public interface AuditlogRepository {
    Auditlog save(Auditlog aggregate);

    Optional<Auditlog> findById(AuditlogId id);

    Optional<Auditlog> findByReferenceCode(String tenantId, String referenceCode);

    List<Auditlog> findByTenant(String tenantId, int limit);

    void delete(AuditlogId id);
}
