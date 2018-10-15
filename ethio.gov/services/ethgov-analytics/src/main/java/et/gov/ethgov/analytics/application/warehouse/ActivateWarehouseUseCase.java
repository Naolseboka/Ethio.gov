package et.gov.ethgov.analytics.application.warehouse;

import et.gov.ethgov.analytics.domain.warehouse.Warehouse;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseId;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateWarehouseUseCase {
    private final WarehouseRepository repository;
    private final Clock clock;

    public ActivateWarehouseUseCase(WarehouseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WarehouseId execute(ActivateWarehouseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        WarehouseId id = WarehouseId.parse(command.warehouseId());
        Warehouse aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("warehouse not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateWarehouseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String warehouseId,
            String reason) {}
}
