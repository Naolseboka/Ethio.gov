package et.gov.ethgov.reporting.application.reportfilter;

import et.gov.ethgov.reporting.domain.reportfilter.Reportfilter;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterId;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendReportfilterUseCase {
    private final ReportfilterRepository repository;
    private final Clock clock;

    public SuspendReportfilterUseCase(ReportfilterRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportfilterId execute(SuspendReportfilterCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReportfilterId id = ReportfilterId.parse(command.reportfilterId());
        Reportfilter aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportfilter not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendReportfilterCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportfilterId,
            String reason) {}
}
