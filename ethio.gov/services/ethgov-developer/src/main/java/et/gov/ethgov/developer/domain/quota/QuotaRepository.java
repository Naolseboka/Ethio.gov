package et.gov.ethgov.developer.domain.quota;

import java.util.List;
import java.util.Optional;

public interface QuotaRepository {
    Quota save(Quota aggregate);

    Optional<Quota> findById(QuotaId id);

    Optional<Quota> findByReferenceCode(String tenantId, String referenceCode);

    List<Quota> findByTenant(String tenantId, int limit);

    void delete(QuotaId id);
}
