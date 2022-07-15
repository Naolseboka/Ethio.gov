package et.gov.ethgov.licensing.application.suspension;

import et.gov.ethgov.licensing.domain.suspension.Suspension;
import et.gov.ethgov.licensing.domain.suspension.SuspensionRepository;
import java.util.List;
import java.util.Objects;

public final class ListSuspensionUseCase {
    private final SuspensionRepository repository;

    public ListSuspensionUseCase(SuspensionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Suspension> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
