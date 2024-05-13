package et.gov.ethgov.permits.application.temporary;

import et.gov.ethgov.permits.domain.temporary.Temporary;
import et.gov.ethgov.permits.domain.temporary.TemporaryId;
import et.gov.ethgov.permits.domain.temporary.TemporaryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendTemporaryUseCase {
    private final TemporaryRepository repository;
    private final Clock clock;

    public SuspendTemporaryUseCase(TemporaryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TemporaryId execute(SuspendTemporaryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TemporaryId id = TemporaryId.parse(command.temporaryId());
        Temporary aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("temporary not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendTemporaryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String temporaryId,
            String reason) {}
}
