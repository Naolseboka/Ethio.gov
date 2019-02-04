package et.gov.ethgov.case.application.closure;

import et.gov.ethgov.case.domain.closure.Closure;
import et.gov.ethgov.case.domain.closure.ClosureId;
import et.gov.ethgov.case.domain.closure.ClosureRepository;
import java.util.Objects;

public final class GetClosureUseCase {
    private final ClosureRepository repository;

    public GetClosureUseCase(ClosureRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Closure execute(String rawId) {
        ClosureId id = ClosureId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("closure not found: " + rawId));
    }
}
