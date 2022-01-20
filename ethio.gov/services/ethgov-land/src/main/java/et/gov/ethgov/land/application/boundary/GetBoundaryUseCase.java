package et.gov.ethgov.land.application.boundary;

import et.gov.ethgov.land.domain.boundary.Boundary;
import et.gov.ethgov.land.domain.boundary.BoundaryId;
import et.gov.ethgov.land.domain.boundary.BoundaryRepository;
import java.util.Objects;

public final class GetBoundaryUseCase {
    private final BoundaryRepository repository;

    public GetBoundaryUseCase(BoundaryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Boundary execute(String rawId) {
        BoundaryId id = BoundaryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("boundary not found: " + rawId));
    }
}
