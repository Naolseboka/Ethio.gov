package et.gov.ethgov.workflow.domain.escalation;

import java.util.List;
import java.util.Optional;

public interface EscalationRepository {
    Escalation save(Escalation aggregate);

    Optional<Escalation> findById(EscalationId id);

    Optional<Escalation> findByReferenceCode(String tenantId, String referenceCode);

    List<Escalation> findByTenant(String tenantId, int limit);

    void delete(EscalationId id);
}
