package et.gov.ethgov.notification.application.digest;

import et.gov.ethgov.notification.domain.digest.Digest;
import et.gov.ethgov.notification.domain.digest.DigestId;
import et.gov.ethgov.notification.domain.digest.DigestRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendDigestUseCase {
    private final DigestRepository repository;
    private final Clock clock;

    public SuspendDigestUseCase(DigestRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DigestId execute(SuspendDigestCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DigestId id = DigestId.parse(command.digestId());
        Digest aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("digest not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendDigestCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String digestId,
            String reason) {}
}
