package et.gov.ethgov.directory.domain.publiclisting;

import java.util.List;
import java.util.Optional;

public interface PubliclistingRepository {
    Publiclisting save(Publiclisting aggregate);

    Optional<Publiclisting> findById(PubliclistingId id);

    Optional<Publiclisting> findByReferenceCode(String tenantId, String referenceCode);

    List<Publiclisting> findByTenant(String tenantId, int limit);

    void delete(PubliclistingId id);
}
