package et.gov.ethgov.gateway.domain.healthcheck;

import java.util.List;
import java.util.Optional;

public interface HealthcheckRepository {
    Healthcheck save(Healthcheck aggregate);

    Optional<Healthcheck> findById(HealthcheckId id);

    Optional<Healthcheck> findByReferenceCode(String tenantId, String referenceCode);

    List<Healthcheck> findByTenant(String tenantId, int limit);

    void delete(HealthcheckId id);
}
