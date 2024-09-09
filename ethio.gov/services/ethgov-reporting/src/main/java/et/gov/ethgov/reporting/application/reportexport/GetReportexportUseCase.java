package et.gov.ethgov.reporting.application.reportexport;

import et.gov.ethgov.reporting.domain.reportexport.Reportexport;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportId;
import et.gov.ethgov.reporting.domain.reportexport.ReportexportRepository;
import java.util.Objects;

public final class GetReportexportUseCase {
    private final ReportexportRepository repository;

    public GetReportexportUseCase(ReportexportRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Reportexport execute(String rawId) {
        ReportexportId id = ReportexportId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportexport not found: " + rawId));
    }
}
