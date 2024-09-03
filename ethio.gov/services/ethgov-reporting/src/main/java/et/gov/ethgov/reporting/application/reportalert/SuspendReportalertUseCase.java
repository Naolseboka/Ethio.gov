package et.gov.ethgov.reporting.application.reportalert;

import et.gov.ethgov.reporting.domain.reportalert.Reportalert;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertId;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendReportalertUseCase {
    private final ReportalertRepository repository;
    private final Clock clock;

    public SuspendReportalertUseCase(ReportalertRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportalertId execute(SuspendReportalertCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReportalertId id = ReportalertId.parse(command.reportalertId());
        Reportalert aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportalert not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendReportalertCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportalertId,
            String reason) {}
}
