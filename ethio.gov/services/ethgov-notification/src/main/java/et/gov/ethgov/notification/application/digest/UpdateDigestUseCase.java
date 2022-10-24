package et.gov.ethgov.notification.application.digest;

import et.gov.ethgov.notification.domain.digest.Digest;
import et.gov.ethgov.notification.domain.digest.DigestId;
import et.gov.ethgov.notification.domain.digest.DigestRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDigestUseCase {
    private final DigestRepository repository;
    private final Clock clock;

    public UpdateDigestUseCase(DigestRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DigestId execute(UpdateDigestCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DigestId id = DigestId.parse(command.digestId());
        Digest aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("digest not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDigestCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String digestId,
            String reason) {}
}
