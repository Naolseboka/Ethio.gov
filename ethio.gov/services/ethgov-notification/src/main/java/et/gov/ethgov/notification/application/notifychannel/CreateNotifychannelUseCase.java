package et.gov.ethgov.notification.application.notifychannel;

import et.gov.ethgov.notification.domain.notifychannel.Notifychannel;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelId;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateNotifychannelUseCase {
    private final NotifychannelRepository repository;
    private final Clock clock;

    public CreateNotifychannelUseCase(NotifychannelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public NotifychannelId execute(CreateNotifychannelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Notifychannel aggregate = Notifychannel.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateNotifychannelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String notifychannelId,
            String reason) {}
}
