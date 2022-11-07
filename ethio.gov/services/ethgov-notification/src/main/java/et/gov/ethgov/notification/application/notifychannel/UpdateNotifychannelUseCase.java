package et.gov.ethgov.notification.application.notifychannel;

import et.gov.ethgov.notification.domain.notifychannel.Notifychannel;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelId;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateNotifychannelUseCase {
    private final NotifychannelRepository repository;
    private final Clock clock;

    public UpdateNotifychannelUseCase(NotifychannelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public NotifychannelId execute(UpdateNotifychannelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        NotifychannelId id = NotifychannelId.parse(command.notifychannelId());
        Notifychannel aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("notifychannel not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateNotifychannelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String notifychannelId,
            String reason) {}
}
