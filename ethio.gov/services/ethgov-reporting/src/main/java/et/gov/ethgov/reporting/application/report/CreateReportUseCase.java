package et.gov.ethgov.reporting.application.report;

import et.gov.ethgov.reporting.domain.report.Report;
import et.gov.ethgov.reporting.domain.report.ReportId;
import et.gov.ethgov.reporting.domain.report.ReportRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateReportUseCase {
    private final ReportRepository repository;
    private final Clock clock;

    public CreateReportUseCase(ReportRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportId execute(CreateReportCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Report aggregate = Report.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateReportCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportId,
            String reason) {}
}
