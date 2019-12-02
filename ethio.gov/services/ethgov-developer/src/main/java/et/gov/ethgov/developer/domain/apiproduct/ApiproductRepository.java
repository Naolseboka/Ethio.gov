package et.gov.ethgov.developer.domain.apiproduct;

import java.util.List;
import java.util.Optional;

public interface ApiproductRepository {
    Apiproduct save(Apiproduct aggregate);

    Optional<Apiproduct> findById(ApiproductId id);

    Optional<Apiproduct> findByReferenceCode(String tenantId, String referenceCode);

    List<Apiproduct> findByTenant(String tenantId, int limit);

    void delete(ApiproductId id);
}
