package et.gov.ethgov.permits.application.temporary;

import et.gov.ethgov.permits.domain.temporary.Temporary;
import et.gov.ethgov.permits.domain.temporary.TemporaryId;
import et.gov.ethgov.permits.domain.temporary.TemporaryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTemporaryUseCase {
    private final TemporaryRepository repository;
    private final Clock clock;

    public CreateTemporaryUseCase(TemporaryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TemporaryId execute(CreateTemporaryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Temporary aggregate = Temporary.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTemporaryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String temporaryId,
            String reason) {}
}
