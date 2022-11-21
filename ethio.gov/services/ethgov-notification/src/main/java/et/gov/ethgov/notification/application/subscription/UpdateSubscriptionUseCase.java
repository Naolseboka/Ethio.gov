package et.gov.ethgov.notification.application.subscription;

import et.gov.ethgov.notification.domain.subscription.Subscription;
import et.gov.ethgov.notification.domain.subscription.SubscriptionId;
import et.gov.ethgov.notification.domain.subscription.SubscriptionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSubscriptionUseCase {
    private final SubscriptionRepository repository;
    private final Clock clock;

    public UpdateSubscriptionUseCase(SubscriptionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SubscriptionId execute(UpdateSubscriptionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SubscriptionId id = SubscriptionId.parse(command.subscriptionId());
        Subscription aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("subscription not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSubscriptionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String subscriptionId,
            String reason) {}
}
