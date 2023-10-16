package et.gov.ethgov.organization.domain.rolebinding;

import java.util.List;
import java.util.Optional;

public interface RolebindingRepository {
    Rolebinding save(Rolebinding aggregate);

    Optional<Rolebinding> findById(RolebindingId id);

    Optional<Rolebinding> findByReferenceCode(String tenantId, String referenceCode);

    List<Rolebinding> findByTenant(String tenantId, int limit);

    void delete(RolebindingId id);
}
