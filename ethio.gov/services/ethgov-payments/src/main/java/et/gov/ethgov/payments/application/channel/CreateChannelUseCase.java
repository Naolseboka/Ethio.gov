package et.gov.ethgov.payments.application.channel;

import et.gov.ethgov.payments.domain.channel.Channel;
import et.gov.ethgov.payments.domain.channel.ChannelId;
import et.gov.ethgov.payments.domain.channel.ChannelRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateChannelUseCase {
    private final ChannelRepository repository;
    private final Clock clock;

    public CreateChannelUseCase(ChannelRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ChannelId execute(CreateChannelCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Channel aggregate = Channel.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateChannelCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String channelId,
            String reason) {}
}
