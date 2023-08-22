package et.gov.ethgov.organization.application.successor;

import et.gov.ethgov.organization.domain.successor.Successor;
import et.gov.ethgov.organization.domain.successor.SuccessorId;
import et.gov.ethgov.organization.domain.successor.SuccessorRepository;
import java.util.Objects;

public final class GetSuccessorUseCase {
    private final SuccessorRepository repository;

    public GetSuccessorUseCase(SuccessorRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Successor execute(String rawId) {
        SuccessorId id = SuccessorId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("successor not found: " + rawId));
    }
}
