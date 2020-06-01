package et.gov.ethgov.directory.domain.queue;

import java.util.List;
import java.util.Optional;

public interface QueueRepository {
    Queue save(Queue aggregate);

    Optional<Queue> findById(QueueId id);

    Optional<Queue> findByReferenceCode(String tenantId, String referenceCode);

    List<Queue> findByTenant(String tenantId, int limit);

    void delete(QueueId id);
}
