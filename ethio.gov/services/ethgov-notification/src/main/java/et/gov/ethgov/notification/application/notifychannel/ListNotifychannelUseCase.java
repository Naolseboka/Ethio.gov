package et.gov.ethgov.notification.application.notifychannel;

import et.gov.ethgov.notification.domain.notifychannel.Notifychannel;
import et.gov.ethgov.notification.domain.notifychannel.NotifychannelRepository;
import java.util.List;
import java.util.Objects;

public final class ListNotifychannelUseCase {
    private final NotifychannelRepository repository;

    public ListNotifychannelUseCase(NotifychannelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Notifychannel> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
