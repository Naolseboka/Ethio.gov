package et.gov.ethgov.licensing.application.condition;

import et.gov.ethgov.licensing.domain.condition.Condition;
import et.gov.ethgov.licensing.domain.condition.ConditionId;
import et.gov.ethgov.licensing.domain.condition.ConditionRepository;
import java.util.Objects;

public final class GetConditionUseCase {
    private final ConditionRepository repository;

    public GetConditionUseCase(ConditionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Condition execute(String rawId) {
        ConditionId id = ConditionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("condition not found: " + rawId));
    }
}
