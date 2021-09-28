package et.gov.ethgov.identity.application.lockout;

import et.gov.ethgov.identity.domain.lockout.Lockout;
import et.gov.ethgov.identity.domain.lockout.LockoutRepository;
import java.util.List;
import java.util.Objects;

public final class ListLockoutUseCase {
    private final LockoutRepository repository;

    public ListLockoutUseCase(LockoutRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Lockout> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
