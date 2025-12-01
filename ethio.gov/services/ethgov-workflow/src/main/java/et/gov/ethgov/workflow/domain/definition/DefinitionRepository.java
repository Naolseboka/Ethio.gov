package et.gov.ethgov.workflow.domain.definition;

import java.util.List;
import java.util.Optional;

public interface DefinitionRepository {
    Definition save(Definition aggregate);

    Optional<Definition> findById(DefinitionId id);

    Optional<Definition> findByReferenceCode(String tenantId, String referenceCode);

    List<Definition> findByTenant(String tenantId, int limit);

    void delete(DefinitionId id);
}
