package et.gov.ethgov.developer.application.devcredential;

import et.gov.ethgov.developer.domain.devcredential.Devcredential;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialId;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDevcredentialUseCase {
    private final DevcredentialRepository repository;
    private final Clock clock;

    public ActivateDevcredentialUseCase(DevcredentialRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevcredentialId execute(ActivateDevcredentialCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DevcredentialId id = DevcredentialId.parse(command.devcredentialId());
        Devcredential aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devcredential not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDevcredentialCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devcredentialId,
            String reason) {}
}
