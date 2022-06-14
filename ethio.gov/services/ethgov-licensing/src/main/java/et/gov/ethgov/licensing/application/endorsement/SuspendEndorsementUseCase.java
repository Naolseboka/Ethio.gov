package et.gov.ethgov.licensing.application.endorsement;

import et.gov.ethgov.licensing.domain.endorsement.Endorsement;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementId;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendEndorsementUseCase {
    private final EndorsementRepository repository;
    private final Clock clock;

    public SuspendEndorsementUseCase(EndorsementRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EndorsementId execute(SuspendEndorsementCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EndorsementId id = EndorsementId.parse(command.endorsementId());
        Endorsement aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("endorsement not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendEndorsementCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String endorsementId,
            String reason) {}
}
