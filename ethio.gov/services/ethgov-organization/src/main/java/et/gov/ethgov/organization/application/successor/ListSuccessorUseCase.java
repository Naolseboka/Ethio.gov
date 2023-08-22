package et.gov.ethgov.organization.application.successor;

import et.gov.ethgov.organization.domain.successor.Successor;
import et.gov.ethgov.organization.domain.successor.SuccessorRepository;
import java.util.List;
import java.util.Objects;

public final class ListSuccessorUseCase {
    private final SuccessorRepository repository;

    public ListSuccessorUseCase(SuccessorRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Successor> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
