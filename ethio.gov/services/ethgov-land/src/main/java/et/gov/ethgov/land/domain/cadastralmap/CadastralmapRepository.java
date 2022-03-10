package et.gov.ethgov.land.domain.cadastralmap;

import java.util.List;
import java.util.Optional;

public interface CadastralmapRepository {
    Cadastralmap save(Cadastralmap aggregate);

    Optional<Cadastralmap> findById(CadastralmapId id);

    Optional<Cadastralmap> findByReferenceCode(String tenantId, String referenceCode);

    List<Cadastralmap> findByTenant(String tenantId, int limit);

    void delete(CadastralmapId id);
}
