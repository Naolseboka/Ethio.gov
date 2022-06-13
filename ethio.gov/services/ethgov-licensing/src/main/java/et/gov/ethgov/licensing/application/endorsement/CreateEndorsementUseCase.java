package et.gov.ethgov.licensing.application.endorsement;

import et.gov.ethgov.licensing.domain.endorsement.Endorsement;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementId;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateEndorsementUseCase {
    private final EndorsementRepository repository;
    private final Clock clock;

    public CreateEndorsementUseCase(EndorsementRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EndorsementId execute(CreateEndorsementCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Endorsement aggregate = Endorsement.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateEndorsementCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String endorsementId,
            String reason) {}
}
