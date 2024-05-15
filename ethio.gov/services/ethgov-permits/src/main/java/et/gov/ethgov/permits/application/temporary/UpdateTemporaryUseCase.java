package et.gov.ethgov.permits.application.temporary;

import et.gov.ethgov.permits.domain.temporary.Temporary;
import et.gov.ethgov.permits.domain.temporary.TemporaryId;
import et.gov.ethgov.permits.domain.temporary.TemporaryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateTemporaryUseCase {
    private final TemporaryRepository repository;
    private final Clock clock;

    public UpdateTemporaryUseCase(TemporaryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TemporaryId execute(UpdateTemporaryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TemporaryId id = TemporaryId.parse(command.temporaryId());
        Temporary aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("temporary not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateTemporaryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String temporaryId,
            String reason) {}
}
