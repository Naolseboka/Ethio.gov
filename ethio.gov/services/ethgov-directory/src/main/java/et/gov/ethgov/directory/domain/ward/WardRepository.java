package et.gov.ethgov.directory.domain.ward;

import java.util.List;
import java.util.Optional;

public interface WardRepository {
    Ward save(Ward aggregate);

    Optional<Ward> findById(WardId id);

    Optional<Ward> findByReferenceCode(String tenantId, String referenceCode);

    List<Ward> findByTenant(String tenantId, int limit);

    void delete(WardId id);
}
