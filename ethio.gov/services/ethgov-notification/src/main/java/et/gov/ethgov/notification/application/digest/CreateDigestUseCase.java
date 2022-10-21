package et.gov.ethgov.notification.application.digest;

import et.gov.ethgov.notification.domain.digest.Digest;
import et.gov.ethgov.notification.domain.digest.DigestId;
import et.gov.ethgov.notification.domain.digest.DigestRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDigestUseCase {
    private final DigestRepository repository;
    private final Clock clock;

    public CreateDigestUseCase(DigestRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DigestId execute(CreateDigestCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Digest aggregate = Digest.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDigestCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String digestId,
            String reason) {}
}
