package et.gov.ethgov.workflow.domain.timer;

import java.util.List;
import java.util.Optional;

public interface TimerRepository {
    Timer save(Timer aggregate);

    Optional<Timer> findById(TimerId id);

    Optional<Timer> findByReferenceCode(String tenantId, String referenceCode);

    List<Timer> findByTenant(String tenantId, int limit);

    void delete(TimerId id);
}
