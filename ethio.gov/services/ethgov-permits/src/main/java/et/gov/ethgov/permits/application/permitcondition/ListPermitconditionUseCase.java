package et.gov.ethgov.permits.application.permitcondition;

import et.gov.ethgov.permits.domain.permitcondition.Permitcondition;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionRepository;
import java.util.List;
import java.util.Objects;

public final class ListPermitconditionUseCase {
    private final PermitconditionRepository repository;

    public ListPermitconditionUseCase(PermitconditionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Permitcondition> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
