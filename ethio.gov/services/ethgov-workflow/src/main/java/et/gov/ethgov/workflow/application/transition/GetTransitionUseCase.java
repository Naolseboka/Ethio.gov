package et.gov.ethgov.workflow.application.transition;

import et.gov.ethgov.workflow.domain.transition.Transition;
import et.gov.ethgov.workflow.domain.transition.TransitionId;
import et.gov.ethgov.workflow.domain.transition.TransitionRepository;
import java.util.Objects;

public final class GetTransitionUseCase {
    private final TransitionRepository repository;

    public GetTransitionUseCase(TransitionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Transition execute(String rawId) {
        TransitionId id = TransitionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("transition not found: " + rawId));
    }
}
