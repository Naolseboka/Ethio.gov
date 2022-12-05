package et.gov.ethgov.notification.domain.delivery;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {
    Delivery save(Delivery aggregate);

    Optional<Delivery> findById(DeliveryId id);

    Optional<Delivery> findByReferenceCode(String tenantId, String referenceCode);

    List<Delivery> findByTenant(String tenantId, int limit);

    void delete(DeliveryId id);
}
