package et.gov.ethgov.permits.application.amendment;

import et.gov.ethgov.permits.domain.amendment.Amendment;
import et.gov.ethgov.permits.domain.amendment.AmendmentId;
import et.gov.ethgov.permits.domain.amendment.AmendmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAmendmentUseCase {
    private final AmendmentRepository repository;
    private final Clock clock;

    public CreateAmendmentUseCase(AmendmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AmendmentId execute(CreateAmendmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Amendment aggregate = Amendment.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAmendmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String amendmentId,
            String reason) {}
}
