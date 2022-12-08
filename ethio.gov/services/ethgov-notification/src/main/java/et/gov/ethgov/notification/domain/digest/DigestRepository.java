package et.gov.ethgov.notification.domain.digest;

import java.util.List;
import java.util.Optional;

public interface DigestRepository {
    Digest save(Digest aggregate);

    Optional<Digest> findById(DigestId id);

    Optional<Digest> findByReferenceCode(String tenantId, String referenceCode);

    List<Digest> findByTenant(String tenantId, int limit);

    void delete(DigestId id);
}
