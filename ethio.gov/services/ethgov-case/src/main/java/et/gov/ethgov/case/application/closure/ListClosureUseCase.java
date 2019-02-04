package et.gov.ethgov.case.application.closure;

import et.gov.ethgov.case.domain.closure.Closure;
import et.gov.ethgov.case.domain.closure.ClosureRepository;
import java.util.List;
import java.util.Objects;

public final class ListClosureUseCase {
    private final ClosureRepository repository;

    public ListClosureUseCase(ClosureRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Closure> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
