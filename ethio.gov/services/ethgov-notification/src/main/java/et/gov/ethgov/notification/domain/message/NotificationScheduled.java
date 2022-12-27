package et.gov.ethgov.notification.domain.message;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record NotificationScheduled(
        NotificationId notificationId,
        TenantId tenantId,
        NotificationNumber notificationNumber,
        NotificationChannel channel,
        Instant occurredAt)
        implements NotificationDomainEvent {}
