package et.gov.ethgov.reporting.domain.reportsubscription;

import java.util.List;
import java.util.Optional;

public interface ReportsubscriptionRepository {
    Reportsubscription save(Reportsubscription aggregate);

    Optional<Reportsubscription> findById(ReportsubscriptionId id);

    Optional<Reportsubscription> findByReferenceCode(String tenantId, String referenceCode);

    List<Reportsubscription> findByTenant(String tenantId, int limit);

    void delete(ReportsubscriptionId id);
}
