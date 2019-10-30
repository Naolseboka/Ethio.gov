package et.gov.ethgov.developer.application.devcredential;

import et.gov.ethgov.developer.domain.devcredential.Devcredential;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialId;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendDevcredentialUseCase {
    private final DevcredentialRepository repository;
    private final Clock clock;

    public SuspendDevcredentialUseCase(DevcredentialRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevcredentialId execute(SuspendDevcredentialCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DevcredentialId id = DevcredentialId.parse(command.devcredentialId());
        Devcredential aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devcredential not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendDevcredentialCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devcredentialId,
            String reason) {}
}
