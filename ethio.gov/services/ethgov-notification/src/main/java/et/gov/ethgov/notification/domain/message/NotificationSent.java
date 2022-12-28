package et.gov.ethgov.notification.domain.message;
import java.time.Instant;
public record NotificationSent(NotificationId notificationId, String providerReference, Instant occurredAt)
        implements NotificationDomainEvent {}
