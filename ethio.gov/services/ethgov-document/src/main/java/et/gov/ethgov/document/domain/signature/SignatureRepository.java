package et.gov.ethgov.document.domain.signature;

import java.util.List;
import java.util.Optional;

public interface SignatureRepository {
    Signature save(Signature aggregate);

    Optional<Signature> findById(SignatureId id);

    Optional<Signature> findByReferenceCode(String tenantId, String referenceCode);

    List<Signature> findByTenant(String tenantId, int limit);

    void delete(SignatureId id);
}
