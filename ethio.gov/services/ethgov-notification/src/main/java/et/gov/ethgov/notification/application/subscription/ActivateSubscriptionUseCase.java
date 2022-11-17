package et.gov.ethgov.notification.application.subscription;

import et.gov.ethgov.notification.domain.subscription.Subscription;
import et.gov.ethgov.notification.domain.subscription.SubscriptionId;
import et.gov.ethgov.notification.domain.subscription.SubscriptionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSubscriptionUseCase {
    private final SubscriptionRepository repository;
    private final Clock clock;

    public ActivateSubscriptionUseCase(SubscriptionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SubscriptionId execute(ActivateSubscriptionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SubscriptionId id = SubscriptionId.parse(command.subscriptionId());
        Subscription aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("subscription not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSubscriptionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String subscriptionId,
            String reason) {}
}
