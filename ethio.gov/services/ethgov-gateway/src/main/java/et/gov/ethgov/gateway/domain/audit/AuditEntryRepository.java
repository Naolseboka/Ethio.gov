package et.gov.ethgov.gateway.domain.audit;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import java.util.Optional;

public interface AuditEntryRepository {
    void append(AuditEntry entry);

    Optional<AuditEntry> findById(AuditEntryId id);

    List<AuditEntry> findRecentByTenant(TenantId tenantId, int limit);
}
