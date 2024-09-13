package et.gov.ethgov.reporting.application.reportsubscription;

import et.gov.ethgov.reporting.domain.reportsubscription.Reportsubscription;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionRepository;
import java.util.List;
import java.util.Objects;

public final class ListReportsubscriptionUseCase {
    private final ReportsubscriptionRepository repository;

    public ListReportsubscriptionUseCase(ReportsubscriptionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Reportsubscription> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
