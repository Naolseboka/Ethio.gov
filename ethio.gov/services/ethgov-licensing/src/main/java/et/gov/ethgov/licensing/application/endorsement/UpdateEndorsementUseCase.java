package et.gov.ethgov.licensing.application.endorsement;

import et.gov.ethgov.licensing.domain.endorsement.Endorsement;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementId;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateEndorsementUseCase {
    private final EndorsementRepository repository;
    private final Clock clock;

    public UpdateEndorsementUseCase(EndorsementRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EndorsementId execute(UpdateEndorsementCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EndorsementId id = EndorsementId.parse(command.endorsementId());
        Endorsement aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("endorsement not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateEndorsementCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String endorsementId,
            String reason) {}
}
