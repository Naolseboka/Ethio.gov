package et.gov.ethgov.permits.application.permitchecklist;

import et.gov.ethgov.permits.domain.permitchecklist.Permitchecklist;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistId;
import et.gov.ethgov.permits.domain.permitchecklist.PermitchecklistRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPermitchecklistUseCase {
    private final PermitchecklistRepository repository;
    private final Clock clock;

    public SuspendPermitchecklistUseCase(PermitchecklistRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitchecklistId execute(SuspendPermitchecklistCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PermitchecklistId id = PermitchecklistId.parse(command.permitchecklistId());
        Permitchecklist aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitchecklist not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPermitchecklistCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitchecklistId,
            String reason) {}
}
