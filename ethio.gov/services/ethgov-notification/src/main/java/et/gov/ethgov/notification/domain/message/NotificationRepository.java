package et.gov.ethgov.notification.domain.message;

import et.gov.ethgov.platform.commons.tenant.TenantId;

import java.util.Optional;

public interface NotificationRepository {
    void save(OutboundNotification notification);
    Optional<OutboundNotification> findById(NotificationId notificationId);
    Optional<OutboundNotification> findByTenantAndNotificationNumber(
            TenantId tenantId, NotificationNumber notificationNumber);
    boolean existsByTenantAndNotificationNumber(TenantId tenantId, NotificationNumber notificationNumber);
}
