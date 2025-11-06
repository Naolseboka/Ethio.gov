package et.gov.ethgov.workflow.application.transition;

import et.gov.ethgov.workflow.domain.transition.Transition;
import et.gov.ethgov.workflow.domain.transition.TransitionRepository;
import java.util.List;
import java.util.Objects;

public final class ListTransitionUseCase {
    private final TransitionRepository repository;

    public ListTransitionUseCase(TransitionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Transition> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
