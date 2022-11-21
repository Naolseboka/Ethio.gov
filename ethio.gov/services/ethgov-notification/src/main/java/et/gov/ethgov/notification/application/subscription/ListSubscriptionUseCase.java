package et.gov.ethgov.notification.application.subscription;

import et.gov.ethgov.notification.domain.subscription.Subscription;
import et.gov.ethgov.notification.domain.subscription.SubscriptionRepository;
import java.util.List;
import java.util.Objects;

public final class ListSubscriptionUseCase {
    private final SubscriptionRepository repository;

    public ListSubscriptionUseCase(SubscriptionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Subscription> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
