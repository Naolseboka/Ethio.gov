package et.gov.ethgov.reporting.application.reportfilter;

import et.gov.ethgov.reporting.domain.reportfilter.Reportfilter;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterId;
import et.gov.ethgov.reporting.domain.reportfilter.ReportfilterRepository;
import java.util.Objects;

public final class GetReportfilterUseCase {
    private final ReportfilterRepository repository;

    public GetReportfilterUseCase(ReportfilterRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Reportfilter execute(String rawId) {
        ReportfilterId id = ReportfilterId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportfilter not found: " + rawId));
    }
}
