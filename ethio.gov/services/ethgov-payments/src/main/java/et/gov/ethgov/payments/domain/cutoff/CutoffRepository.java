package et.gov.ethgov.payments.domain.cutoff;

import java.util.List;
import java.util.Optional;

public interface CutoffRepository {
    Cutoff save(Cutoff aggregate);

    Optional<Cutoff> findById(CutoffId id);

    Optional<Cutoff> findByReferenceCode(String tenantId, String referenceCode);

    List<Cutoff> findByTenant(String tenantId, int limit);

    void delete(CutoffId id);
}
