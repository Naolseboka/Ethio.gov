package et.gov.ethgov.notification.application.subscription;

import et.gov.ethgov.notification.domain.subscription.Subscription;
import et.gov.ethgov.notification.domain.subscription.SubscriptionId;
import et.gov.ethgov.notification.domain.subscription.SubscriptionRepository;
import java.util.Objects;

public final class GetSubscriptionUseCase {
    private final SubscriptionRepository repository;

    public GetSubscriptionUseCase(SubscriptionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Subscription execute(String rawId) {
        SubscriptionId id = SubscriptionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("subscription not found: " + rawId));
    }
}
