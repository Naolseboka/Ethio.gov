package et.gov.ethgov.document.domain.redaction;

import java.util.List;
import java.util.Optional;

public interface RedactionRepository {
    Redaction save(Redaction aggregate);

    Optional<Redaction> findById(RedactionId id);

    Optional<Redaction> findByReferenceCode(String tenantId, String referenceCode);

    List<Redaction> findByTenant(String tenantId, int limit);

    void delete(RedactionId id);
}
