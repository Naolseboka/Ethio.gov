package et.gov.ethgov.land.application.boundary;

import et.gov.ethgov.land.domain.boundary.Boundary;
import et.gov.ethgov.land.domain.boundary.BoundaryRepository;
import java.util.List;
import java.util.Objects;

public final class ListBoundaryUseCase {
    private final BoundaryRepository repository;

    public ListBoundaryUseCase(BoundaryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Boundary> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
