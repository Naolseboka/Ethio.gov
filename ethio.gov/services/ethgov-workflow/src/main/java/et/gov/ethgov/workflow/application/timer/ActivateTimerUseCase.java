package et.gov.ethgov.workflow.application.timer;

import et.gov.ethgov.workflow.domain.timer.Timer;
import et.gov.ethgov.workflow.domain.timer.TimerId;
import et.gov.ethgov.workflow.domain.timer.TimerRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateTimerUseCase {
    private final TimerRepository repository;
    private final Clock clock;

    public ActivateTimerUseCase(TimerRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TimerId execute(ActivateTimerCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TimerId id = TimerId.parse(command.timerId());
        Timer aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("timer not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateTimerCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String timerId,
            String reason) {}
}
