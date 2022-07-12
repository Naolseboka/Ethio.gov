package et.gov.ethgov.licensing.application.suspension;

import et.gov.ethgov.licensing.domain.suspension.Suspension;
import et.gov.ethgov.licensing.domain.suspension.SuspensionId;
import et.gov.ethgov.licensing.domain.suspension.SuspensionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSuspensionUseCase {
    private final SuspensionRepository repository;
    private final Clock clock;

    public ActivateSuspensionUseCase(SuspensionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SuspensionId execute(ActivateSuspensionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SuspensionId id = SuspensionId.parse(command.suspensionId());
        Suspension aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("suspension not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSuspensionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String suspensionId,
            String reason) {}
}
