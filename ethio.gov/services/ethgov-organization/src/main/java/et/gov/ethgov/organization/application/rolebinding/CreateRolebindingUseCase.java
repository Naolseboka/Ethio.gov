package et.gov.ethgov.organization.application.rolebinding;

import et.gov.ethgov.organization.domain.rolebinding.Rolebinding;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingId;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRolebindingUseCase {
    private final RolebindingRepository repository;
    private final Clock clock;

    public CreateRolebindingUseCase(RolebindingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RolebindingId execute(CreateRolebindingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Rolebinding aggregate = Rolebinding.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRolebindingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String rolebindingId,
            String reason) {}
}
