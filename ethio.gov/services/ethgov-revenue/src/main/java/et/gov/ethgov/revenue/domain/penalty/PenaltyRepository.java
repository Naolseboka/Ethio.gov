package et.gov.ethgov.revenue.domain.penalty;

import java.util.List;
import java.util.Optional;

public interface PenaltyRepository {
    Penalty save(Penalty aggregate);

    Optional<Penalty> findById(PenaltyId id);

    Optional<Penalty> findByReferenceCode(String tenantId, String referenceCode);

    List<Penalty> findByTenant(String tenantId, int limit);

    void delete(PenaltyId id);
}
