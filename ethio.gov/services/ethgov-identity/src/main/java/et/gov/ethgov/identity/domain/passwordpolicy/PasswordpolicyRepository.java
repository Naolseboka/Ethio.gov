package et.gov.ethgov.identity.domain.passwordpolicy;

import java.util.List;
import java.util.Optional;

public interface PasswordpolicyRepository {
    Passwordpolicy save(Passwordpolicy aggregate);

    Optional<Passwordpolicy> findById(PasswordpolicyId id);

    Optional<Passwordpolicy> findByReferenceCode(String tenantId, String referenceCode);

    List<Passwordpolicy> findByTenant(String tenantId, int limit);

    void delete(PasswordpolicyId id);
}
