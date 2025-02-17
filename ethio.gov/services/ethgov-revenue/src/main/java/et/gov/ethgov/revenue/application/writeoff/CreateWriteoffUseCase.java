package et.gov.ethgov.revenue.application.writeoff;

import et.gov.ethgov.revenue.domain.writeoff.Writeoff;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffId;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateWriteoffUseCase {
    private final WriteoffRepository repository;
    private final Clock clock;

    public CreateWriteoffUseCase(WriteoffRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WriteoffId execute(CreateWriteoffCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Writeoff aggregate = Writeoff.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateWriteoffCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String writeoffId,
            String reason) {}
}
