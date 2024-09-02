package et.gov.ethgov.reporting.application.report;

import et.gov.ethgov.reporting.domain.report.Report;
import et.gov.ethgov.reporting.domain.report.ReportId;
import et.gov.ethgov.reporting.domain.report.ReportRepository;
import java.util.Objects;

public final class GetReportUseCase {
    private final ReportRepository repository;

    public GetReportUseCase(ReportRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Report execute(String rawId) {
        ReportId id = ReportId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("report not found: " + rawId));
    }
}
