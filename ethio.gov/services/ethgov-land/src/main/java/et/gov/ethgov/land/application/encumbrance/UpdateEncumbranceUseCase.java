package et.gov.ethgov.land.application.encumbrance;

import et.gov.ethgov.land.domain.encumbrance.Encumbrance;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceId;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateEncumbranceUseCase {
    private final EncumbranceRepository repository;
    private final Clock clock;

    public UpdateEncumbranceUseCase(EncumbranceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EncumbranceId execute(UpdateEncumbranceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EncumbranceId id = EncumbranceId.parse(command.encumbranceId());
        Encumbrance aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("encumbrance not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateEncumbranceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String encumbranceId,
            String reason) {}
}
