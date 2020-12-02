package et.gov.ethgov.gateway.application.auditlog;

import et.gov.ethgov.gateway.domain.auditlog.Auditlog;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogId;
import et.gov.ethgov.gateway.domain.auditlog.AuditlogRepository;
import java.util.Objects;

public final class GetAuditlogUseCase {
    private final AuditlogRepository repository;

    public GetAuditlogUseCase(AuditlogRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Auditlog execute(String rawId) {
        AuditlogId id = AuditlogId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("auditlog not found: " + rawId));
    }
}
