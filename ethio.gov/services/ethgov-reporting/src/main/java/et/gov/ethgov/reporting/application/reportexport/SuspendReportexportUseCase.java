package et.gov.ethgov.reporting.application.reportexport;

import et.gov.ethgov.reporting.domain.reportexport.Reportexport;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportId;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendReportexportUseCase {
    private final ReportexportRepository repository;
    private final Clock clock;

    public SuspendReportexportUseCase(ReportexportRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportexportId execute(SuspendReportexportCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReportexportId id = ReportexportId.parse(command.reportexportId());
        Reportexport aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportexport not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendReportexportCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportexportId,
            String reason) {}
}
