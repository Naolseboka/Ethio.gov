package et.gov.ethgov.notification.application.delivery;

import et.gov.ethgov.notification.domain.delivery.Delivery;
import et.gov.ethgov.notification.domain.delivery.DeliveryId;
import et.gov.ethgov.notification.domain.delivery.DeliveryRepository;
import java.util.Objects;

public final class GetDeliveryUseCase {
    private final DeliveryRepository repository;

    public GetDeliveryUseCase(DeliveryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Delivery execute(String rawId) {
        DeliveryId id = DeliveryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("delivery not found: " + rawId));
    }
}
