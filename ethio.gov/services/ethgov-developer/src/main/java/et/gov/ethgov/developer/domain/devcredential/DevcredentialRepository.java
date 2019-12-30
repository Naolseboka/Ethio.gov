package et.gov.ethgov.developer.domain.devcredential;

import java.util.List;
import java.util.Optional;

public interface DevcredentialRepository {
    Devcredential save(Devcredential aggregate);

    Optional<Devcredential> findById(DevcredentialId id);

    Optional<Devcredential> findByReferenceCode(String tenantId, String referenceCode);

    List<Devcredential> findByTenant(String tenantId, int limit);

    void delete(DevcredentialId id);
}
