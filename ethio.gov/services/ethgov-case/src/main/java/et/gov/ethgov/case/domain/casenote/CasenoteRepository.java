package et.gov.ethgov.case.domain.casenote;

import java.util.List;
import java.util.Optional;

public interface CasenoteRepository {
    Casenote save(Casenote aggregate);

    Optional<Casenote> findById(CasenoteId id);

    Optional<Casenote> findByReferenceCode(String tenantId, String referenceCode);

    List<Casenote> findByTenant(String tenantId, int limit);

    void delete(CasenoteId id);
}
