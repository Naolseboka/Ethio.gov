package et.gov.ethgov.reporting.application.reportalert;

import et.gov.ethgov.reporting.domain.reportalert.Reportalert;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertId;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateReportalertUseCase {
    private final ReportalertRepository repository;
    private final Clock clock;

    public ActivateReportalertUseCase(ReportalertRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportalertId execute(ActivateReportalertCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReportalertId id = ReportalertId.parse(command.reportalertId());
        Reportalert aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportalert not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateReportalertCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportalertId,
            String reason) {}
}
