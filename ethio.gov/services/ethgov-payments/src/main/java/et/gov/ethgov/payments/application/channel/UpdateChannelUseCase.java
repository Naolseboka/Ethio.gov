package et.gov.ethgov.payments.application.channel;

import et.gov.ethgov.payments.domain.channel.Channel;
import et.gov.ethgov.payments.domain.channel.ChannelId;
import et.gov.ethgov.payments.domain.channel.ChannelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateChannelUseCase {
    private final ChannelRepository repository;
    private final Clock clock;

    public UpdateChannelUseCase(ChannelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ChannelId execute(UpdateChannelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ChannelId id = ChannelId.parse(command.channelId());
        Channel aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("channel not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateChannelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String channelId,
            String reason) {}
}
