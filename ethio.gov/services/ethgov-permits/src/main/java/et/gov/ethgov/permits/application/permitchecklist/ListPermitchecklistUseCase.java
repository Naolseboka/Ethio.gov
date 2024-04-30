package et.gov.ethgov.permits.application.permitchecklist;

import et.gov.ethgov.permits.domain.permitchecklist.Permitchecklist;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistRepository;
import java.util.List;
import java.util.Objects;

public final class ListPermitchecklistUseCase {
    private final PermitchecklistRepository repository;

    public ListPermitchecklistUseCase(PermitchecklistRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Permitchecklist> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
