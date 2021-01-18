package et.gov.ethgov.gateway.application.upstream;

import et.gov.ethgov.gateway.domain.upstream.Upstream;
import et.gov.ethgov.gateway.domain.upstream.UpstreamId;
import et.gov.ethgov.gateway.domain.upstream.UpstreamRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateUpstreamUseCase {
    private final UpstreamRepository repository;
    private final Clock clock;

    public CreateUpstreamUseCase(UpstreamRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public UpstreamId execute(CreateUpstreamCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Upstream aggregate = Upstream.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateUpstreamCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String upstreamId,
            String reason) {}
}
