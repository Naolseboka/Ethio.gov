package et.gov.ethgov.payments.application.channel;

import et.gov.ethgov.payments.domain.channel.Channel;
import et.gov.ethgov.payments.domain.channel.ChannelRepository;
import java.util.List;
import java.util.Objects;

public final class ListChannelUseCase {
    private final ChannelRepository repository;

    public ListChannelUseCase(ChannelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Channel> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
