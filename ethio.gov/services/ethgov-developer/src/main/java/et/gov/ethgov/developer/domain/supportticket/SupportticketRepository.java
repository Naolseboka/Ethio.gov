package et.gov.ethgov.developer.domain.supportticket;

import java.util.List;
import java.util.Optional;

public interface SupportticketRepository {
    Supportticket save(Supportticket aggregate);

    Optional<Supportticket> findById(SupportticketId id);

    Optional<Supportticket> findByReferenceCode(String tenantId, String referenceCode);

    List<Supportticket> findByTenant(String tenantId, int limit);

    void delete(SupportticketId id);
}
