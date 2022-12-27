package et.gov.ethgov.notification.domain.message;

import java.time.Instant;

public sealed interface NotificationDomainEvent
        permits NotificationScheduled, NotificationSent, NotificationDelivered,
                NotificationFailed, NotificationCancelled {
    NotificationId notificationId();
    Instant occurredAt();
}
