package et.gov.ethgov.notification.application.delivery;

import et.gov.ethgov.notification.domain.delivery.Delivery;
import et.gov.ethgov.notification.domain.delivery.DeliveryRepository;
import java.util.List;
import java.util.Objects;

public final class ListDeliveryUseCase {
    private final DeliveryRepository repository;

    public ListDeliveryUseCase(DeliveryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Delivery> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
