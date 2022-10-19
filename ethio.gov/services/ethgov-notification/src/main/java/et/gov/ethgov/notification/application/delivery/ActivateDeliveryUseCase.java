package et.gov.ethgov.notification.application.delivery;

import et.gov.ethgov.notification.domain.delivery.Delivery;
import et.gov.ethgov.notification.domain.delivery.DeliveryId;
import et.gov.ethgov.notification.domain.delivery.DeliveryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDeliveryUseCase {
    private final DeliveryRepository repository;
    private final Clock clock;

    public ActivateDeliveryUseCase(DeliveryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DeliveryId execute(ActivateDeliveryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DeliveryId id = DeliveryId.parse(command.deliveryId());
        Delivery aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("delivery not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDeliveryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String deliveryId,
            String reason) {}
}
