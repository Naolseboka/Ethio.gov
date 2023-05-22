package et.gov.ethgov.opendata.domain.opendataset;

import java.util.List;
import java.util.Optional;

public interface OpendatasetRepository {
    Opendataset save(Opendataset aggregate);

    Optional<Opendataset> findById(OpendatasetId id);

    Optional<Opendataset> findByReferenceCode(String tenantId, String referenceCode);

    List<Opendataset> findByTenant(String tenantId, int limit);

    void delete(OpendatasetId id);
}
