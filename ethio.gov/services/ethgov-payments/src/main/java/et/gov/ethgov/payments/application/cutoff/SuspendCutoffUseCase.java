package et.gov.ethgov.payments.application.cutoff;

import et.gov.ethgov.payments.domain.cutoff.Cutoff;
import et.gov.ethgov.payments.domain.cutoff.CutoffId;
import et.gov.ethgov.payments.domain.cutoff.CutoffRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendCutoffUseCase {
    private final CutoffRepository repository;
    private final Clock clock;

    public SuspendCutoffUseCase(CutoffRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CutoffId execute(SuspendCutoffCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CutoffId id = CutoffId.parse(command.cutoffId());
        Cutoff aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cutoff not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendCutoffCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cutoffId,
            String reason) {}
}
