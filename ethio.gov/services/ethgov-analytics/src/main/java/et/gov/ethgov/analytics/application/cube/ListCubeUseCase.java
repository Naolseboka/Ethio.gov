package et.gov.ethgov.analytics.application.cube;

import et.gov.ethgov.analytics.domain.cube.Cube;
import et.gov.ethgov.analytics.domain.cube.CubeRepository;
import java.util.List;
import java.util.Objects;

public final class ListCubeUseCase {
    private final CubeRepository repository;

    public ListCubeUseCase(CubeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Cube> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
