package et.gov.ethgov.document.application.retention;

import et.gov.ethgov.document.domain.retention.Retention;
import et.gov.ethgov.document.domain.retention.RetentionRepository;
import java.util.List;
import java.util.Objects;

public final class ListRetentionUseCase {
    private final RetentionRepository repository;

    public ListRetentionUseCase(RetentionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Retention> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
