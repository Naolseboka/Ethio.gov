package et.gov.ethgov.permits.application.permit;

import et.gov.ethgov.permits.domain.permit.Permit;
import et.gov.ethgov.permits.domain.permit.PermitRepository;
import java.util.List;
import java.util.Objects;

public final class ListPermitUseCase {
    private final PermitRepository repository;

    public ListPermitUseCase(PermitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Permit> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
