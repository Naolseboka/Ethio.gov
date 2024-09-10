package et.gov.ethgov.reporting.application.reportfilter;

import et.gov.ethgov.reporting.domain.reportfilter.Reportfilter;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterId;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateReportfilterUseCase {
    private final ReportfilterRepository repository;
    private final Clock clock;

    public CreateReportfilterUseCase(ReportfilterRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReportfilterId execute(CreateReportfilterCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Reportfilter aggregate = Reportfilter.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateReportfilterCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String reportfilterId,
            String reason) {}
}
