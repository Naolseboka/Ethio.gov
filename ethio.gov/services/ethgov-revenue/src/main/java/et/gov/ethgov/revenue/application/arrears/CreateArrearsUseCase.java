package et.gov.ethgov.revenue.application.arrears;

import et.gov.ethgov.revenue.domain.arrears.Arrears;
import et.gov.ethgov.revenue.domain.arrears.ArrearsId;
import et.gov.ethgov.revenue.domain.arrears.ArrearsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateArrearsUseCase {
    private final ArrearsRepository repository;
    private final Clock clock;

    public CreateArrearsUseCase(ArrearsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ArrearsId execute(CreateArrearsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Arrears aggregate = Arrears.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateArrearsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String arrearsId,
            String reason) {}
}
