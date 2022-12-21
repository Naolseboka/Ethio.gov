package et.gov.ethgov.notification.domain.message;
import java.time.Instant;
public record NotificationDelivered(NotificationId notificationId, Instant occurredAt)
        implements NotificationDomainEvent {}
