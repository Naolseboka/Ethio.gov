package et.gov.ethgov.land.application.easement;

import et.gov.ethgov.land.domain.easement.Easement;
import et.gov.ethgov.land.domain.easement.EasementId;
import et.gov.ethgov.land.domain.easement.EasementRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateEasementUseCase {
    private final EasementRepository repository;
    private final Clock clock;

    public CreateEasementUseCase(EasementRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EasementId execute(CreateEasementCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Easement aggregate = Easement.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateEasementCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String easementId,
            String reason) {}
}
