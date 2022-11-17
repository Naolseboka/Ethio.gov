package et.gov.ethgov.notification.application.subscription;

import et.gov.ethgov.notification.domain.subscription.Subscription;
import et.gov.ethgov.notification.domain.subscription.SubscriptionId;
import et.gov.ethgov.notification.domain.subscription.SubscriptionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSubscriptionUseCase {
    private final SubscriptionRepository repository;
    private final Clock clock;

    public CreateSubscriptionUseCase(SubscriptionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SubscriptionId execute(CreateSubscriptionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Subscription aggregate = Subscription.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSubscriptionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String subscriptionId,
            String reason) {}
}
