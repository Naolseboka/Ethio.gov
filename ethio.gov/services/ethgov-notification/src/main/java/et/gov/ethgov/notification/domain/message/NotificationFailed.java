package et.gov.ethgov.notification.domain.message;
import java.time.Instant;
public record NotificationFailed(NotificationId notificationId, String reason, Instant occurredAt)
        implements NotificationDomainEvent {}
