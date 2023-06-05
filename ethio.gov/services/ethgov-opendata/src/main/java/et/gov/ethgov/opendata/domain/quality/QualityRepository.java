package et.gov.ethgov.opendata.domain.quality;

import java.util.List;
import java.util.Optional;

public interface QualityRepository {
    Quality save(Quality aggregate);

    Optional<Quality> findById(QualityId id);

    Optional<Quality> findByReferenceCode(String tenantId, String referenceCode);

    List<Quality> findByTenant(String tenantId, int limit);

    void delete(QualityId id);
}
