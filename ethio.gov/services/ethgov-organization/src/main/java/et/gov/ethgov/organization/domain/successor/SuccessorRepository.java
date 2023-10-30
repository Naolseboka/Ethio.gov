package et.gov.ethgov.organization.domain.successor;

import java.util.List;
import java.util.Optional;

public interface SuccessorRepository {
    Successor save(Successor aggregate);

    Optional<Successor> findById(SuccessorId id);

    Optional<Successor> findByReferenceCode(String tenantId, String referenceCode);

    List<Successor> findByTenant(String tenantId, int limit);

    void delete(SuccessorId id);
}
