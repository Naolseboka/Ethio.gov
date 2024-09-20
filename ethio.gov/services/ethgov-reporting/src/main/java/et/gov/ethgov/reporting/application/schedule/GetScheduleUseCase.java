package et.gov.ethgov.reporting.application.schedule;

import et.gov.ethgov.reporting.domain.schedule.Schedule;
import et.gov.ethgov.reporting.domain.schedule.ScheduleId;
import et.gov.ethgov.reporting.domain.schedule.ScheduleRepository;
import java.util.Objects;

public final class GetScheduleUseCase {
    private final ScheduleRepository repository;

    public GetScheduleUseCase(ScheduleRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Schedule execute(String rawId) {
        ScheduleId id = ScheduleId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("schedule not found: " + rawId));
    }
}
