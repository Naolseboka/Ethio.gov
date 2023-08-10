package et.gov.ethgov.organization.application.rolebinding;

import et.gov.ethgov.organization.domain.rolebinding.Rolebinding;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingId;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateRolebindingUseCase {
    private final RolebindingRepository repository;
    private final Clock clock;

    public ActivateRolebindingUseCase(RolebindingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RolebindingId execute(ActivateRolebindingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RolebindingId id = RolebindingId.parse(command.rolebindingId());
        Rolebinding aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("rolebinding not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateRolebindingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String rolebindingId,
            String reason) {}
}
