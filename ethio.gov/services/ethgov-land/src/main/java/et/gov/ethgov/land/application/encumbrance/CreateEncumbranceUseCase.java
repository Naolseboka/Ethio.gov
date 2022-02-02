package et.gov.ethgov.land.application.encumbrance;

import et.gov.ethgov.land.domain.encumbrance.Encumbrance;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceId;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateEncumbranceUseCase {
    private final EncumbranceRepository repository;
    private final Clock clock;

    public CreateEncumbranceUseCase(EncumbranceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EncumbranceId execute(CreateEncumbranceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Encumbrance aggregate = Encumbrance.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateEncumbranceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String encumbranceId,
            String reason) {}
}
