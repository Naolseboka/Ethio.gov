package et.gov.ethgov.reporting.application.kpi;

import et.gov.ethgov.reporting.domain.kpi.Kpi;
import et.gov.ethgov.reporting.domain.kpi.KpiId;
import et.gov.ethgov.reporting.domain.kpi.KpiRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateKpiUseCase {
    private final KpiRepository repository;
    private final Clock clock;

    public ActivateKpiUseCase(KpiRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public KpiId execute(ActivateKpiCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        KpiId id = KpiId.parse(command.kpiId());
        Kpi aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("kpi not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateKpiCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String kpiId,
            String reason) {}
}
