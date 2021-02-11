package et.gov.ethgov.gateway.domain.circuitbreaker;

import java.util.List;
import java.util.Optional;

public interface CircuitbreakerRepository {
    Circuitbreaker save(Circuitbreaker aggregate);

    Optional<Circuitbreaker> findById(CircuitbreakerId id);

    Optional<Circuitbreaker> findByReferenceCode(String tenantId, String referenceCode);

    List<Circuitbreaker> findByTenant(String tenantId, int limit);

    void delete(CircuitbreakerId id);
}
