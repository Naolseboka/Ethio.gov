package et.gov.ethgov.gateway.application.auditlog;

import et.gov.ethgov.gateway.domain.auditlog.Auditlog;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogRepository;
import java.util.List;
import java.util.Objects;

public final class ListAuditlogUseCase {
    private final AuditlogRepository repository;

    public ListAuditlogUseCase(AuditlogRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Auditlog> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
