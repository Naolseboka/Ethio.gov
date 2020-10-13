package et.gov.ethgov.document.domain.renderjob;

import java.util.List;
import java.util.Optional;

public interface RenderjobRepository {
    Renderjob save(Renderjob aggregate);

    Optional<Renderjob> findById(RenderjobId id);

    Optional<Renderjob> findByReferenceCode(String tenantId, String referenceCode);

    List<Renderjob> findByTenant(String tenantId, int limit);

    void delete(RenderjobId id);
}
