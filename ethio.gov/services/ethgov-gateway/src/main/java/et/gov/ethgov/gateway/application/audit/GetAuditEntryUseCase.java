package et.gov.ethgov.gateway.application.audit;

import et.gov.ethgov.gateway.domain.audit.AuditEntry;
import et.gov.ethgov.gateway.domain.audit.AuditEntryId;
import et.gov.ethgov.gateway.domain.audit.AuditEntryRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAuditEntryUseCase {
    private final AuditEntryRepository audits;

    public GetAuditEntryUseCase(AuditEntryRepository audits) {
        this.audits = audits;
    }

    @Transactional(readOnly = true)
    public AuditEntry byId(String auditEntryId) {
        return audits
                .findById(AuditEntryId.of(auditEntryId))
                .orElseThrow(() -> new DomainException(
                        "AUDIT_NOT_FOUND", "Audit Not Found", "Unknown audit entry " + auditEntryId, 404));
    }

    @Transactional(readOnly = true)
    public List<AuditEntry> recentByTenant(String tenantId, int limit) {
        int capped = Math.min(Math.max(limit, 1), 100);
        return audits.findRecentByTenant(TenantId.of(tenantId), capped);
    }
}
