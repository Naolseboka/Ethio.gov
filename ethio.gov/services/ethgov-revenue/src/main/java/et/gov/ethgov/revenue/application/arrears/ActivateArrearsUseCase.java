package et.gov.ethgov.revenue.application.arrears;

import et.gov.ethgov.revenue.domain.arrears.Arrears;
import et.gov.ethgov.revenue.domain.arrears.ArrearsId;
import et.gov.ethgov.revenue.domain.arrears.ArrearsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateArrearsUseCase {
    private final ArrearsRepository repository;
    private final Clock clock;

    public ActivateArrearsUseCase(ArrearsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ArrearsId execute(ActivateArrearsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ArrearsId id = ArrearsId.parse(command.arrearsId());
        Arrears aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("arrears not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateArrearsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String arrearsId,
            String reason) {}
}
