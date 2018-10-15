package et.gov.ethgov.analytics.application.warehouse;

import et.gov.ethgov.analytics.domain.warehouse.Warehouse;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseRepository;
import java.util.List;
import java.util.Objects;

public final class ListWarehouseUseCase {
    private final WarehouseRepository repository;

    public ListWarehouseUseCase(WarehouseRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Warehouse> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
