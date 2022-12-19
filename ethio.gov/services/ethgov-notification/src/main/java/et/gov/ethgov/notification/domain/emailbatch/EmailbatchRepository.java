package et.gov.ethgov.notification.domain.emailbatch;

import java.util.List;
import java.util.Optional;

public interface EmailbatchRepository {
    Emailbatch save(Emailbatch aggregate);

    Optional<Emailbatch> findById(EmailbatchId id);

    Optional<Emailbatch> findByReferenceCode(String tenantId, String referenceCode);

    List<Emailbatch> findByTenant(String tenantId, int limit);

    void delete(EmailbatchId id);
}
