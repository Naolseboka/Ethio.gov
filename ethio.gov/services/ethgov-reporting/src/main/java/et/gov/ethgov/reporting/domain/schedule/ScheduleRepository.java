package et.gov.ethgov.reporting.domain.schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    Schedule save(Schedule aggregate);

    Optional<Schedule> findById(ScheduleId id);

    Optional<Schedule> findByReferenceCode(String tenantId, String referenceCode);

    List<Schedule> findByTenant(String tenantId, int limit);

    void delete(ScheduleId id);
}
