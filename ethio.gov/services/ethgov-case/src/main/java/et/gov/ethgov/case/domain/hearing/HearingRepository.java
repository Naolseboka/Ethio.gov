package et.gov.ethgov.case.domain.hearing;

import java.util.List;
import java.util.Optional;

public interface HearingRepository {
    Hearing save(Hearing aggregate);

    Optional<Hearing> findById(HearingId id);

    Optional<Hearing> findByReferenceCode(String tenantId, String referenceCode);

    List<Hearing> findByTenant(String tenantId, int limit);

    void delete(HearingId id);
}
