package et.gov.ethgov.reporting.application.reportexport;

import et.gov.ethgov.reporting.domain.reportexport.Reportexport;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportId;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateReportexportUseCase {
    private final ReportexportRepository repository;
    private final Clock clock;

    public ActivateReportexportUseCase(ReportexportRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportexportId execute(ActivateReportexportCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReportexportId id = ReportexportId.parse(command.reportexportId());
        Reportexport aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportexport not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateReportexportCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportexportId,
            String reason) {}
}
