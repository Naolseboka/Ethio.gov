package et.gov.ethgov.gateway.domain.mtls;

import java.util.List;
import java.util.Optional;

public interface MtlsRepository {
    Mtls save(Mtls aggregate);

    Optional<Mtls> findById(MtlsId id);

    Optional<Mtls> findByReferenceCode(String tenantId, String referenceCode);

    List<Mtls> findByTenant(String tenantId, int limit);

    void delete(MtlsId id);
}
