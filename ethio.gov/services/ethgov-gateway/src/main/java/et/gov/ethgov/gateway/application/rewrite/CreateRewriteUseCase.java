package et.gov.ethgov.gateway.application.rewrite;

import et.gov.ethgov.gateway.domain.rewrite.Rewrite;
import et.gov.ethgov.gateway.domain.rewrite.RewriteId;
import et.gov.ethgov.gateway.domain.rewrite.RewriteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRewriteUseCase {
    private final RewriteRepository repository;
    private final Clock clock;

    public CreateRewriteUseCase(RewriteRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RewriteId execute(CreateRewriteCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Rewrite aggregate = Rewrite.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRewriteCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String rewriteId,
            String reason) {}
}
