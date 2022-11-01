package et.gov.ethgov.notification.application.message;

import et.gov.ethgov.notification.domain.message.NotificationId;
import et.gov.ethgov.notification.domain.message.NotificationNumber;
import et.gov.ethgov.notification.domain.message.NotificationRepository;
import et.gov.ethgov.notification.domain.message.OutboundNotification;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNotificationUseCase {

    private final NotificationRepository notifications;

    public GetNotificationUseCase(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public OutboundNotification byId(String notificationId) {
        return notifications.findById(NotificationId.of(notificationId))
                .orElseThrow(() -> new DomainException(
                        "NOTIFICATION_NOT_FOUND",
                        "Notification Not Found",
                        "Notification does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public OutboundNotification byNumber(String tenantId, String notificationNumber) {
        return notifications
                .findByTenantAndNotificationNumber(
                        TenantId.of(tenantId), NotificationNumber.of(notificationNumber))
                .orElseThrow(() -> new DomainException(
                        "NOTIFICATION_NOT_FOUND",
                        "Notification Not Found",
                        "Notification does not exist",
                        404));
    }
}
