package et.gov.ethgov.reporting.application.run;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.reporting.application.ReportingEventPublisher;
import et.gov.ethgov.reporting.domain.run.ReportRun;
import et.gov.ethgov.reporting.domain.run.ReportRunId;
import et.gov.ethgov.reporting.domain.run.ReportRunRepository;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StartReportRunUseCase {

    private final ReportRunRepository runs;
    private final ReportingEventPublisher eventPublisher;
    private final Clock clock;

    public StartReportRunUseCase(ReportRunRepository runs, ReportingEventPublisher eventPublisher, Clock clock) {
        this.runs = runs;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String reportRunId) {
        Objects.requireNonNull(reportRunId, "reportRunId");
        ReportRun run = runs.findById(ReportRunId.of(reportRunId))
                .orElseThrow(() -> new DomainException(
                        "RUN_NOT_FOUND", "Run Not Found", "Report run does not exist", 404));
        run.start(clock.instant());
        runs.save(run);
        eventPublisher.publishRunEvents(run.pullDomainEvents());
    }
}
