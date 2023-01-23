package et.gov.ethgov.notification.domain.subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {
    Subscription save(Subscription aggregate);

    Optional<Subscription> findById(SubscriptionId id);

    Optional<Subscription> findByReferenceCode(String tenantId, String referenceCode);

    List<Subscription> findByTenant(String tenantId, int limit);

    void delete(SubscriptionId id);
}
