package et.gov.ethgov.case.domain.casefile;

import java.util.List;
import java.util.Optional;

public interface CasefileRepository {
    Casefile save(Casefile aggregate);

    Optional<Casefile> findById(CasefileId id);

    Optional<Casefile> findByReferenceCode(String tenantId, String referenceCode);

    List<Casefile> findByTenant(String tenantId, int limit);

    void delete(CasefileId id);
}
