package et.gov.ethgov.organization.application.rolebinding;

import et.gov.ethgov.organization.domain.rolebinding.Rolebinding;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingId;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendRolebindingUseCase {
    private final RolebindingRepository repository;
    private final Clock clock;

    public SuspendRolebindingUseCase(RolebindingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RolebindingId execute(SuspendRolebindingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RolebindingId id = RolebindingId.parse(command.rolebindingId());
        Rolebinding aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("rolebinding not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendRolebindingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String rolebindingId,
            String reason) {}
}
