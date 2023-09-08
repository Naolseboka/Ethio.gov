package et.gov.ethgov.organization.domain.mandate;

import java.util.List;
import java.util.Optional;

public interface MandateRepository {
    Mandate save(Mandate aggregate);

    Optional<Mandate> findById(MandateId id);

    Optional<Mandate> findByReferenceCode(String tenantId, String referenceCode);

    List<Mandate> findByTenant(String tenantId, int limit);

    void delete(MandateId id);
}
