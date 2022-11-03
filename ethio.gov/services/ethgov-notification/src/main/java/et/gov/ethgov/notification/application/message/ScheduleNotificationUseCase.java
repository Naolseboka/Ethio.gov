package et.gov.ethgov.notification.application.message;

import et.gov.ethgov.notification.application.NotificationEventPublisher;
import et.gov.ethgov.notification.domain.message.NotificationChannel;
import et.gov.ethgov.notification.domain.message.NotificationId;
import et.gov.ethgov.notification.domain.message.NotificationLocale;
import et.gov.ethgov.notification.domain.message.NotificationNumber;
import et.gov.ethgov.notification.domain.message.NotificationNumberSequence;
import et.gov.ethgov.notification.domain.message.NotificationRepository;
import et.gov.ethgov.notification.domain.message.OutboundNotification;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleNotificationUseCase {

    private final NotificationRepository notifications;
    private final NotificationNumberSequence sequences;
    private final NotificationEventPublisher eventPublisher;
    private final Clock clock;

    public ScheduleNotificationUseCase(
            NotificationRepository notifications,
            NotificationNumberSequence sequences,
            NotificationEventPublisher eventPublisher,
            Clock clock) {
        this.notifications = notifications;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public NotificationId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        NotificationNumber number = NotificationNumber.allocate(year, sequences.nextSequence(tenantId, year));
        OutboundNotification notification = OutboundNotification.schedule(
                tenantId,
                number,
                NotificationChannel.valueOf(command.channel()),
                command.phone(),
                command.email(),
                command.deviceToken(),
                NotificationLocale.of(command.locale() == null || command.locale().isBlank()
                        ? "en"
                        : command.locale()),
                command.subject(),
                command.body(),
                command.templateCode(),
                command.citizenId(),
                command.organizationId(),
                command.sourceReference(),
                clock.instant());
        notifications.save(notification);
        eventPublisher.publishAll(notification.pullDomainEvents());
        return notification.id();
    }

    public record Command(
            String tenantId,
            String channel,
            String phone,
            String email,
            String deviceToken,
            String locale,
            String subject,
            String body,
            String templateCode,
            String citizenId,
            String organizationId,
            String sourceReference) {}
}
