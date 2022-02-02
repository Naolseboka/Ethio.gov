package et.gov.ethgov.land.application.encumbrance;

import et.gov.ethgov.land.domain.encumbrance.Encumbrance;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceId;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateEncumbranceUseCase {
    private final EncumbranceRepository repository;
    private final Clock clock;

    public ActivateEncumbranceUseCase(EncumbranceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EncumbranceId execute(ActivateEncumbranceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EncumbranceId id = EncumbranceId.parse(command.encumbranceId());
        Encumbrance aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("encumbrance not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateEncumbranceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String encumbranceId,
            String reason) {}
}
