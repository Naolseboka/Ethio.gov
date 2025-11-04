package et.gov.ethgov.workflow.application.timer;

import et.gov.ethgov.workflow.domain.timer.Timer;
import et.gov.ethgov.workflow.domain.timer.TimerRepository;
import java.util.List;
import java.util.Objects;

public final class ListTimerUseCase {
    private final TimerRepository repository;

    public ListTimerUseCase(TimerRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Timer> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
