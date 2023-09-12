package et.gov.ethgov.organization.domain.membership;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository {
    Membership save(Membership aggregate);

    Optional<Membership> findById(MembershipId id);

    Optional<Membership> findByReferenceCode(String tenantId, String referenceCode);

    List<Membership> findByTenant(String tenantId, int limit);

    void delete(MembershipId id);
}
