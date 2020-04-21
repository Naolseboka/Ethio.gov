package et.gov.ethgov.directory.domain.counter;

import java.util.List;
import java.util.Optional;

public interface CounterRepository {
    Counter save(Counter aggregate);

    Optional<Counter> findById(CounterId id);

    Optional<Counter> findByReferenceCode(String tenantId, String referenceCode);

    List<Counter> findByTenant(String tenantId, int limit);

    void delete(CounterId id);
}
