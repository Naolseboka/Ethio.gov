package et.gov.ethgov.permits.application.occupancy;

import et.gov.ethgov.permits.domain.occupancy.Occupancy;
import et.gov.ethgov.permits.domain.occupancy.OccupancyRepository;
import java.util.List;
import java.util.Objects;

public final class ListOccupancyUseCase {
    private final OccupancyRepository repository;

    public ListOccupancyUseCase(OccupancyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Occupancy> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
