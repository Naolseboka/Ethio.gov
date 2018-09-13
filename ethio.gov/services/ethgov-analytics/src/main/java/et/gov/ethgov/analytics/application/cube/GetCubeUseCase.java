package et.gov.ethgov.analytics.application.cube;

import et.gov.ethgov.analytics.domain.cube.Cube;
import et.gov.ethgov.analytics.domain.cube.CubeId;
import et.gov.ethgov.analytics.domain.cube.CubeRepository;
import java.util.Objects;

public final class GetCubeUseCase {
    private final CubeRepository repository;

    public GetCubeUseCase(CubeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Cube execute(String rawId) {
        CubeId id = CubeId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cube not found: " + rawId));
    }
}
