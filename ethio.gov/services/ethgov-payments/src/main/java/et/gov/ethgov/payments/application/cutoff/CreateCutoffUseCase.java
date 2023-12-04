package et.gov.ethgov.payments.application.cutoff;

import et.gov.ethgov.payments.domain.cutoff.Cutoff;
import et.gov.ethgov.payments.domain.cutoff.CutoffId;
import et.gov.ethgov.payments.domain.cutoff.CutoffRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCutoffUseCase {
    private final CutoffRepository repository;
    private final Clock clock;

    public CreateCutoffUseCase(CutoffRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CutoffId execute(CreateCutoffCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Cutoff aggregate = Cutoff.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCutoffCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cutoffId,
            String reason) {}
}
