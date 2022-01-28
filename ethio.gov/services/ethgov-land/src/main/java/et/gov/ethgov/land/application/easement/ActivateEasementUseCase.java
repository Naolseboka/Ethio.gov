package et.gov.ethgov.land.application.easement;

import et.gov.ethgov.land.domain.easement.Easement;
import et.gov.ethgov.land.domain.easement.EasementId;
import et.gov.ethgov.land.domain.easement.EasementRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateEasementUseCase {
    private final EasementRepository repository;
    private final Clock clock;

    public ActivateEasementUseCase(EasementRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EasementId execute(ActivateEasementCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EasementId id = EasementId.parse(command.easementId());
        Easement aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("easement not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateEasementCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String easementId,
            String reason) {}
}
