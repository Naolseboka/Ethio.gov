package et.gov.ethgov.directory.domain.office;

import java.util.List;
import java.util.Optional;

public interface OfficeRepository {
    Office save(Office aggregate);

    Optional<Office> findById(OfficeId id);

    Optional<Office> findByReferenceCode(String tenantId, String referenceCode);

    List<Office> findByTenant(String tenantId, int limit);

    void delete(OfficeId id);
}
