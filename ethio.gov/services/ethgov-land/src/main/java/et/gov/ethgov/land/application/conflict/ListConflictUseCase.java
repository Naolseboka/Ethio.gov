package et.gov.ethgov.land.application.conflict;

import et.gov.ethgov.land.domain.conflict.Conflict;
import et.gov.ethgov.land.domain.conflict.ConflictRepository;
import java.util.List;
import java.util.Objects;

public final class ListConflictUseCase {
    private final ConflictRepository repository;

    public ListConflictUseCase(ConflictRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Conflict> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
