package et.gov.ethgov.reporting.application.schedule;

import et.gov.ethgov.reporting.domain.schedule.Schedule;
import et.gov.ethgov.reporting.domain.schedule.ScheduleRepository;
import java.util.List;
import java.util.Objects;

public final class ListScheduleUseCase {
    private final ScheduleRepository repository;

    public ListScheduleUseCase(ScheduleRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Schedule> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
