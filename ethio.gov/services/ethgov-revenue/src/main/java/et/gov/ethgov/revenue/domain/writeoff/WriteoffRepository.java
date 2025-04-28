package et.gov.ethgov.revenue.domain.writeoff;

import java.util.List;
import java.util.Optional;

public interface WriteoffRepository {
    Writeoff save(Writeoff aggregate);

    Optional<Writeoff> findById(WriteoffId id);

    Optional<Writeoff> findByReferenceCode(String tenantId, String referenceCode);

    List<Writeoff> findByTenant(String tenantId, int limit);

    void delete(WriteoffId id);
}
