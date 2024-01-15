package et.gov.ethgov.payments.domain.channel;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository {
    Channel save(Channel aggregate);

    Optional<Channel> findById(ChannelId id);

    Optional<Channel> findByReferenceCode(String tenantId, String referenceCode);

    List<Channel> findByTenant(String tenantId, int limit);

    void delete(ChannelId id);
}
