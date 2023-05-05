package et.gov.ethgov.opendata.domain.dataupdate;

import java.util.List;
import java.util.Optional;

public interface DataupdateRepository {
    Dataupdate save(Dataupdate aggregate);

    Optional<Dataupdate> findById(DataupdateId id);

    Optional<Dataupdate> findByReferenceCode(String tenantId, String referenceCode);

    List<Dataupdate> findByTenant(String tenantId, int limit);

    void delete(DataupdateId id);
}
