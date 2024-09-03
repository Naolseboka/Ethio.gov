package et.gov.ethgov.reporting.application.reportalert;

import et.gov.ethgov.reporting.domain.reportalert.Reportalert;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertId;
import et.gov.ethgov.reporting.domain.reportalert.ReportalertRepository;
import java.util.Objects;

public final class GetReportalertUseCase {
    private final ReportalertRepository repository;

    public GetReportalertUseCase(ReportalertRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Reportalert execute(String rawId) {
        ReportalertId id = ReportalertId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportalert not found: " + rawId));
    }
}
