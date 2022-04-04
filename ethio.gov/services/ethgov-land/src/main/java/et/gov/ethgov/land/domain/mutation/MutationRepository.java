package et.gov.ethgov.land.domain.mutation;

import java.util.List;
import java.util.Optional;

public interface MutationRepository {
    Mutation save(Mutation aggregate);

    Optional<Mutation> findById(MutationId id);

    Optional<Mutation> findByReferenceCode(String tenantId, String referenceCode);

    List<Mutation> findByTenant(String tenantId, int limit);

    void delete(MutationId id);
}
