package et.gov.ethgov.notification.application;

import et.gov.ethgov.notification.domain.message.NotificationDomainEvent;

public interface NotificationEventPublisher {
    void publishAll(Iterable<NotificationDomainEvent> events);
}
