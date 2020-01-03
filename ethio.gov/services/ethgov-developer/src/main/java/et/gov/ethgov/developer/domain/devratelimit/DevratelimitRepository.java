package et.gov.ethgov.developer.domain.devratelimit;

import java.util.List;
import java.util.Optional;

public interface DevratelimitRepository {
    Devratelimit save(Devratelimit aggregate);

    Optional<Devratelimit> findById(DevratelimitId id);

    Optional<Devratelimit> findByReferenceCode(String tenantId, String referenceCode);

    List<Devratelimit> findByTenant(String tenantId, int limit);

    void delete(DevratelimitId id);
}
