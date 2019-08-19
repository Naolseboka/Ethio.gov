package et.gov.ethgov.citizen.domain.documentlink;

import java.util.List;
import java.util.Optional;

public interface DocumentlinkRepository {
    Documentlink save(Documentlink aggregate);

    Optional<Documentlink> findById(DocumentlinkId id);

    Optional<Documentlink> findByReferenceCode(String tenantId, String referenceCode);

    List<Documentlink> findByTenant(String tenantId, int limit);

    void delete(DocumentlinkId id);
}
