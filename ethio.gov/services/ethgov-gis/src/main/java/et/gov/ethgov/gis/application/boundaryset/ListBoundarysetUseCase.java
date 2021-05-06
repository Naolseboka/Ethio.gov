package et.gov.ethgov.gis.application.boundaryset;

import et.gov.ethgov.gis.domain.boundaryset.Boundaryset;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetRepository;
import java.util.List;
import java.util.Objects;

public final class ListBoundarysetUseCase {
    private final BoundarysetRepository repository;

    public ListBoundarysetUseCase(BoundarysetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Boundaryset> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
