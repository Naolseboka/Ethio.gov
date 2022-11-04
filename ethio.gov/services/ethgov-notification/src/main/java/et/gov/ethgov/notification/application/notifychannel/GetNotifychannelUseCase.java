package et.gov.ethgov.notification.application.notifychannel;

import et.gov.ethgov.notification.domain.notifychannel.Notifychannel;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelId;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelRepository;
import java.util.Objects;

public final class GetNotifychannelUseCase {
    private final NotifychannelRepository repository;

    public GetNotifychannelUseCase(NotifychannelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Notifychannel execute(String rawId) {
        NotifychannelId id = NotifychannelId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("notifychannel not found: " + rawId));
    }
}
