package et.gov.ethgov.revenue.application.writeoff;

import et.gov.ethgov.revenue.domain.writeoff.Writeoff;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffId;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateWriteoffUseCase {
    private final WriteoffRepository repository;
    private final Clock clock;

    public ActivateWriteoffUseCase(WriteoffRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WriteoffId execute(ActivateWriteoffCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        WriteoffId id = WriteoffId.parse(command.writeoffId());
        Writeoff aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("writeoff not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateWriteoffCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String writeoffId,
            String reason) {}
}
