package et.gov.ethgov.workflow.application.timer;

import et.gov.ethgov.workflow.domain.timer.Timer;
import et.gov.ethgov.workflow.domain.timer.TimerId;
import et.gov.ethgov.workflow.domain.timer.TimerRepository;
import java.util.Objects;

public final class GetTimerUseCase {
    private final TimerRepository repository;

    public GetTimerUseCase(TimerRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Timer execute(String rawId) {
        TimerId id = TimerId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("timer not found: " + rawId));
    }
}
