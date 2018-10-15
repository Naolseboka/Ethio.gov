package et.gov.ethgov.analytics.application.warehouse;

import et.gov.ethgov.analytics.domain.warehouse.Warehouse;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseId;
import et.gov.ethgov.analytics.domain.warehouse.WarehouseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateWarehouseUseCase {
    private final WarehouseRepository repository;
    private final Clock clock;

    public CreateWarehouseUseCase(WarehouseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WarehouseId execute(CreateWarehouseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Warehouse aggregate = Warehouse.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateWarehouseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String warehouseId,
            String reason) {}
}
