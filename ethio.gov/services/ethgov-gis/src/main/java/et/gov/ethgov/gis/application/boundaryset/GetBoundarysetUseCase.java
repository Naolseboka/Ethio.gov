package et.gov.ethgov.gis.application.boundaryset;

import et.gov.ethgov.gis.domain.boundaryset.Boundaryset;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetId;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetRepository;
import java.util.Objects;

public final class GetBoundarysetUseCase {
    private final BoundarysetRepository repository;

    public GetBoundarysetUseCase(BoundarysetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Boundaryset execute(String rawId) {
        BoundarysetId id = BoundarysetId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("boundaryset not found: " + rawId));
    }
}
