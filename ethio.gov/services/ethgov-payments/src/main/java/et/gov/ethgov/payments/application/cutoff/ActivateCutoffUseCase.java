package et.gov.ethgov.payments.application.cutoff;

import et.gov.ethgov.payments.domain.cutoff.Cutoff;
import et.gov.ethgov.payments.domain.cutoff.CutoffId;
import et.gov.ethgov.payments.domain.cutoff.CutoffRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCutoffUseCase {
    private final CutoffRepository repository;
    private final Clock clock;

    public ActivateCutoffUseCase(CutoffRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CutoffId execute(ActivateCutoffCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CutoffId id = CutoffId.parse(command.cutoffId());
        Cutoff aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cutoff not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCutoffCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cutoffId,
            String reason) {}
}
