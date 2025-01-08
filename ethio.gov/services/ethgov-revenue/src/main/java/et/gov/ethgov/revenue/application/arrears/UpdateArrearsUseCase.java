package et.gov.ethgov.revenue.application.arrears;

import et.gov.ethgov.revenue.domain.arrears.Arrears;
import et.gov.ethgov.revenue.domain.arrears.ArrearsId;
import et.gov.ethgov.revenue.domain.arrears.ArrearsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateArrearsUseCase {
    private final ArrearsRepository repository;
    private final Clock clock;

    public UpdateArrearsUseCase(ArrearsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ArrearsId execute(UpdateArrearsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ArrearsId id = ArrearsId.parse(command.arrearsId());
        Arrears aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("arrears not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateArrearsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String arrearsId,
            String reason) {}
}
