package et.gov.ethgov.reporting.application.kpi;

import et.gov.ethgov.reporting.domain.kpi.Kpi;
import et.gov.ethgov.reporting.domain.kpi.KpiId;
import et.gov.ethgov.reporting.domain.kpi.KpiRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateKpiUseCase {
    private final KpiRepository repository;
    private final Clock clock;

    public CreateKpiUseCase(KpiRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public KpiId execute(CreateKpiCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Kpi aggregate = Kpi.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateKpiCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String kpiId,
            String reason) {}
}
