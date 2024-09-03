package et.gov.ethgov.reporting.application.report;

import et.gov.ethgov.reporting.domain.report.Report;
import et.gov.ethgov.reporting.domain.report.ReportId;
import et.gov.ethgov.reporting.domain.report.ReportRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateReportUseCase {
    private final ReportRepository repository;
    private final Clock clock;

    public UpdateReportUseCase(ReportRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportId execute(UpdateReportCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReportId id = ReportId.parse(command.reportId());
        Report aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("report not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateReportCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportId,
            String reason) {}
}
