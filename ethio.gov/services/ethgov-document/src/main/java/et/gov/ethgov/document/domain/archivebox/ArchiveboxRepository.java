package et.gov.ethgov.document.domain.archivebox;

import java.util.List;
import java.util.Optional;

public interface ArchiveboxRepository {
    Archivebox save(Archivebox aggregate);

    Optional<Archivebox> findById(ArchiveboxId id);

    Optional<Archivebox> findByReferenceCode(String tenantId, String referenceCode);

    List<Archivebox> findByTenant(String tenantId, int limit);

    void delete(ArchiveboxId id);
}
