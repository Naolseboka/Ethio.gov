package et.gov.ethgov.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.notification.domain.message.NotificationChannel;
import et.gov.ethgov.notification.domain.message.NotificationLocale;
import et.gov.ethgov.notification.domain.message.NotificationNumber;
import et.gov.ethgov.notification.domain.message.NotificationScheduled;
import et.gov.ethgov.notification.domain.message.NotificationStatus;
import et.gov.ethgov.notification.domain.message.OutboundNotification;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OutboundNotificationTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void schedulesSendsAndDeliversSms() {
        OutboundNotification notification = scheduleSms();
        assertThat(notification.status()).isEqualTo(NotificationStatus.PENDING);
        assertThat(notification.pullDomainEvents()).anyMatch(NotificationScheduled.class::isInstance);

        notification.markSent("SMS-GW-1001", now);
        assertThat(notification.status()).isEqualTo(NotificationStatus.SENT);

        notification.markDelivered(now.plusSeconds(30));
        assertThat(notification.status()).isEqualTo(NotificationStatus.DELIVERED);
        assertThatThrownBy(() -> notification.cancel("too late to cancel", now.plusSeconds(60)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void requiresSubjectForEmail() {
        assertThatThrownBy(() -> OutboundNotification.schedule(
                        TenantId.generate(),
                        NotificationNumber.allocate(2024, 1),
                        NotificationChannel.EMAIL,
                        null,
                        "citizen@example.et",
                        null,
                        NotificationLocale.of("en"),
                        null,
                        "Your license was issued",
                        null,
                        null,
                        null,
                        null,
                        now))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private OutboundNotification scheduleSms() {
        return OutboundNotification.schedule(
                TenantId.generate(),
                NotificationNumber.allocate(2024, 1),
                NotificationChannel.SMS,
                "+251911234567",
                null,
                null,
                NotificationLocale.of("am"),
                null,
                "የንግድ ፈቃድዎ ተሰጥቷል",
                "LICENSE_ISSUED",
                "cit_11111111-1111-4111-8111-111111111111",
                null,
                "pay_11111111-1111-4111-8111-111111111111",
                now);
    }
}
