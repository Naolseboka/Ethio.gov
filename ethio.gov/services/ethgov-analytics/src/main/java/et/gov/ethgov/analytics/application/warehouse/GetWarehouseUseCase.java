package et.gov.ethgov.analytics.application.warehouse;

import et.gov.ethgov.analytics.domain.warehouse.Warehouse;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseId;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseRepository;
import java.util.Objects;

public final class GetWarehouseUseCase {
    private final WarehouseRepository repository;

    public GetWarehouseUseCase(WarehouseRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Warehouse execute(String rawId) {
        WarehouseId id = WarehouseId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("warehouse not found: " + rawId));
    }
}
