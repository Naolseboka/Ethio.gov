package et.gov.ethgov.notification.application.message;

import et.gov.ethgov.notification.application.NotificationEventPublisher;
import et.gov.ethgov.notification.domain.message.NotificationId;
import et.gov.ethgov.notification.domain.message.NotificationRepository;
import et.gov.ethgov.notification.domain.message.OutboundNotification;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarkDeliveredUseCase {

    private final NotificationRepository notifications;
    private final NotificationEventPublisher eventPublisher;
    private final Clock clock;

    public MarkDeliveredUseCase(
            NotificationRepository notifications, NotificationEventPublisher eventPublisher, Clock clock) {
        this.notifications = notifications;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String notificationId) {
        Objects.requireNonNull(notificationId, "notificationId");
        OutboundNotification notification = notifications.findById(NotificationId.of(notificationId))
                .orElseThrow(() -> new DomainException(
                        "NOTIFICATION_NOT_FOUND",
                        "Notification Not Found",
                        "Notification does not exist",
                        404));
        notification.markDelivered(clock.instant());
        notifications.save(notification);
        eventPublisher.publishAll(notification.pullDomainEvents());
    }
}
