package et.gov.ethgov.permits.application.permitchecklist;

import et.gov.ethgov.permits.domain.permitchecklist.Permitchecklist;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistId;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePermitchecklistUseCase {
    private final PermitchecklistRepository repository;
    private final Clock clock;

    public ActivatePermitchecklistUseCase(PermitchecklistRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitchecklistId execute(ActivatePermitchecklistCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PermitchecklistId id = PermitchecklistId.parse(command.permitchecklistId());
        Permitchecklist aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitchecklist not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePermitchecklistCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitchecklistId,
            String reason) {}
}
