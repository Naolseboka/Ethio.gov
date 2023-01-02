package et.gov.ethgov.notification.domain.notifychannel;

import java.util.List;
import java.util.Optional;

public interface NotifychannelRepository {
    Notifychannel save(Notifychannel aggregate);

    Optional<Notifychannel> findById(NotifychannelId id);

    Optional<Notifychannel> findByReferenceCode(String tenantId, String referenceCode);

    List<Notifychannel> findByTenant(String tenantId, int limit);

    void delete(NotifychannelId id);
}
