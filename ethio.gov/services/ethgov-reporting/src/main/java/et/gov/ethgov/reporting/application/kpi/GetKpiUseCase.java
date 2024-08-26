package et.gov.ethgov.reporting.application.kpi;

import et.gov.ethgov.reporting.domain.kpi.Kpi;
import et.gov.ethgov.reporting.domain.kpi.KpiId;
import et.gov.ethgov.reporting.domain.kpi.KpiRepository;
import java.util.Objects;

public final class GetKpiUseCase {
    private final KpiRepository repository;

    public GetKpiUseCase(KpiRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Kpi execute(String rawId) {
        KpiId id = KpiId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("kpi not found: " + rawId));
    }
}
