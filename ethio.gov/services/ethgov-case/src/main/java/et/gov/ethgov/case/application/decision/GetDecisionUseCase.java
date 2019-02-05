package et.gov.ethgov.case.application.decision;

import et.gov.ethgov.case.domain.decision.Decision;
import et.gov.ethgov.case.domain.decision.DecisionId;
import et.gov.ethgov.case.domain.decision.DecisionRepository;
import java.util.Objects;

public final class GetDecisionUseCase {
    private final DecisionRepository repository;

    public GetDecisionUseCase(DecisionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Decision execute(String rawId) {
        DecisionId id = DecisionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("decision not found: " + rawId));
    }
}
