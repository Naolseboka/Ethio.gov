package et.gov.ethgov.gateway.application.rewrite;

import et.gov.ethgov.gateway.domain.rewrite.Rewrite;
import et.gov.ethgov.gateway.domain.rewrite.RewriteId;
import et.gov.ethgov.gateway.domain.rewrite.RewriteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateRewriteUseCase {
    private final RewriteRepository repository;
    private final Clock clock;

    public ActivateRewriteUseCase(RewriteRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RewriteId execute(ActivateRewriteCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RewriteId id = RewriteId.parse(command.rewriteId());
        Rewrite aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("rewrite not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateRewriteCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String rewriteId,
            String reason) {}
}
