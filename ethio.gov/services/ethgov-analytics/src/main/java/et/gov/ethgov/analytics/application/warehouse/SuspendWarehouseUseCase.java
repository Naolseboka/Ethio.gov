package et.gov.ethgov.analytics.application.warehouse;

import et.gov.ethgov.analytics.domain.warehouse.Warehouse;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseId;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendWarehouseUseCase {
    private final WarehouseRepository repository;
    private final Clock clock;

    public SuspendWarehouseUseCase(WarehouseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WarehouseId execute(SuspendWarehouseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        WarehouseId id = WarehouseId.parse(command.warehouseId());
        Warehouse aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("warehouse not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendWarehouseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String warehouseId,
            String reason) {}
}
