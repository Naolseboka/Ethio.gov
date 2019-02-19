package et.gov.ethgov.case.application.priority;

import et.gov.ethgov.case.domain.priority.Priority;
import et.gov.ethgov.case.domain.priority.PriorityRepository;
import java.util.List;
import java.util.Objects;

public final class ListPriorityUseCase {
    private final PriorityRepository repository;

    public ListPriorityUseCase(PriorityRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Priority> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
