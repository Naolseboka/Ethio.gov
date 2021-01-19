package et.gov.ethgov.gateway.application.upstream;

import et.gov.ethgov.gateway.domain.upstream.Upstream;
import et.gov.ethgov.gateway.domain.upstream.UpstreamId;
import et.gov.ethgov.gateway.domain.upstream.UpstreamRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateUpstreamUseCase {
    private final UpstreamRepository repository;
    private final Clock clock;

    public UpdateUpstreamUseCase(UpstreamRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public UpstreamId execute(UpdateUpstreamCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        UpstreamId id = UpstreamId.parse(command.upstreamId());
        Upstream aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("upstream not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateUpstreamCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String upstreamId,
            String reason) {}
}
