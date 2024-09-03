package et.gov.ethgov.reporting.application.report;

import et.gov.ethgov.reporting.domain.report.Report;
import et.gov.ethgov.reporting.domain.report.ReportId;
import et.gov.ethgov.reporting.domain.report.ReportRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendReportUseCase {
    private final ReportRepository repository;
    private final Clock clock;

    public SuspendReportUseCase(ReportRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportId execute(SuspendReportCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReportId id = ReportId.parse(command.reportId());
        Report aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("report not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendReportCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportId,
            String reason) {}
}
