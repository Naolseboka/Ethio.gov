package et.gov.ethgov.case.application.priority;

import et.gov.ethgov.case.domain.priority.Priority;
import et.gov.ethgov.case.domain.priority.PriorityId;
import et.gov.ethgov.case.domain.priority.PriorityRepository;
import java.util.Objects;

public final class GetPriorityUseCase {
    private final PriorityRepository repository;

    public GetPriorityUseCase(PriorityRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Priority execute(String rawId) {
        PriorityId id = PriorityId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("priority not found: " + rawId));
    }
}
