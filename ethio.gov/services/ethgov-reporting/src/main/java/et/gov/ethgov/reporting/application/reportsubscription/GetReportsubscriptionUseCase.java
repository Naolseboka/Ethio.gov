package et.gov.ethgov.reporting.application.reportsubscription;

import et.gov.ethgov.reporting.domain.reportsubscription.Reportsubscription;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionId;
import et.gov.ethgov.reporting.domain.reportsubscription.ReportsubscriptionRepository;
import java.util.Objects;

public final class GetReportsubscriptionUseCase {
    private final ReportsubscriptionRepository repository;

    public GetReportsubscriptionUseCase(ReportsubscriptionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Reportsubscription execute(String rawId) {
        ReportsubscriptionId id = ReportsubscriptionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("reportsubscription not found: " + rawId));
    }
}
