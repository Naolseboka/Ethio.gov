package et.gov.ethgov.directory.domain.openinghours;

import java.util.List;
import java.util.Optional;

public interface OpeninghoursRepository {
    Openinghours save(Openinghours aggregate);

    Optional<Openinghours> findById(OpeninghoursId id);

    Optional<Openinghours> findByReferenceCode(String tenantId, String referenceCode);

    List<Openinghours> findByTenant(String tenantId, int limit);

    void delete(OpeninghoursId id);
}
