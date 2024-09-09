package et.gov.ethgov.reporting.application.reportexport;

import et.gov.ethgov.reporting.domain.reportexport.Reportexport;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportId;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateReportexportUseCase {
    private final ReportexportRepository repository;
    private final Clock clock;

    public CreateReportexportUseCase(ReportexportRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportexportId execute(CreateReportexportCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Reportexport aggregate = Reportexport.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateReportexportCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportexportId,
            String reason) {}
}
