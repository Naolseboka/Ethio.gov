package et.gov.ethgov.payments.application.channel;

import et.gov.ethgov.payments.domain.channel.Channel;
import et.gov.ethgov.payments.domain.channel.ChannelId;
import et.gov.ethgov.payments.domain.channel.ChannelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendChannelUseCase {
    private final ChannelRepository repository;
    private final Clock clock;

    public SuspendChannelUseCase(ChannelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ChannelId execute(SuspendChannelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ChannelId id = ChannelId.parse(command.channelId());
        Channel aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("channel not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendChannelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String channelId,
            String reason) {}
}
