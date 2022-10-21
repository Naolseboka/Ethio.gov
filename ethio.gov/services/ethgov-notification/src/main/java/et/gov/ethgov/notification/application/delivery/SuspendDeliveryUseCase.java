package et.gov.ethgov.notification.application.delivery;

import et.gov.ethgov.notification.domain.delivery.Delivery;
import et.gov.ethgov.notification.domain.delivery.DeliveryId;
import et.gov.ethgov.notification.domain.delivery.DeliveryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendDeliveryUseCase {
    private final DeliveryRepository repository;
    private final Clock clock;

    public SuspendDeliveryUseCase(DeliveryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DeliveryId execute(SuspendDeliveryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DeliveryId id = DeliveryId.parse(command.deliveryId());
        Delivery aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("delivery not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendDeliveryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String deliveryId,
            String reason) {}
}
