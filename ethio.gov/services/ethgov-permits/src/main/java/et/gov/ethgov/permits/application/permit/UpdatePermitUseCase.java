package et.gov.ethgov.permits.application.permit;

import et.gov.ethgov.permits.domain.permit.Permit;
import et.gov.ethgov.permits.domain.permit.PermitId;
import et.gov.ethgov.permits.domain.permit.PermitRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePermitUseCase {
    private final PermitRepository repository;
    private final Clock clock;

    public UpdatePermitUseCase(PermitRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitId execute(UpdatePermitCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PermitId id = PermitId.parse(command.permitId());
        Permit aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permit not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePermitCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitId,
            String reason) {}
}
