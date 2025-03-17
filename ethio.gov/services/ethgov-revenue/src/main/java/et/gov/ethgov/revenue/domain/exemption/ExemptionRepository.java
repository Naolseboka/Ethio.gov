package et.gov.ethgov.revenue.domain.exemption;

import java.util.List;
import java.util.Optional;

public interface ExemptionRepository {
    Exemption save(Exemption aggregate);

    Optional<Exemption> findById(ExemptionId id);

    Optional<Exemption> findByReferenceCode(String tenantId, String referenceCode);

    List<Exemption> findByTenant(String tenantId, int limit);

    void delete(ExemptionId id);
}
