package et.gov.ethgov.payments.application.channel;

import et.gov.ethgov.payments.domain.channel.Channel;
import et.gov.ethgov.payments.domain.channel.ChannelId;
import et.gov.ethgov.payments.domain.channel.ChannelRepository;
import java.util.Objects;

public final class GetChannelUseCase {
    private final ChannelRepository repository;

    public GetChannelUseCase(ChannelRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Channel execute(String rawId) {
        ChannelId id = ChannelId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("channel not found: " + rawId));
    }
}
