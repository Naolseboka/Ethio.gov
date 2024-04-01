package et.gov.ethgov.permits.application.amendment;

import et.gov.ethgov.permits.domain.amendment.Amendment;
import et.gov.ethgov.permits.domain.amendment.AmendmentId;
import et.gov.ethgov.permits.domain.amendment.AmendmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateAmendmentUseCase {
    private final AmendmentRepository repository;
    private final Clock clock;

    public UpdateAmendmentUseCase(AmendmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AmendmentId execute(UpdateAmendmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AmendmentId id = AmendmentId.parse(command.amendmentId());
        Amendment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("amendment not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateAmendmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String amendmentId,
            String reason) {}
}
