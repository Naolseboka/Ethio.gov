package et.gov.ethgov.case.application.appeal;

import et.gov.ethgov.case.domain.appeal.Appeal;
import et.gov.ethgov.case.domain.appeal.AppealId;
import et.gov.ethgov.case.domain.appeal.AppealRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateAppealUseCase {
    private final AppealRepository repository;
    private final Clock clock;

    public ActivateAppealUseCase(AppealRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AppealId execute(ActivateAppealCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AppealId id = AppealId.parse(command.appealId());
        Appeal aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("appeal not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateAppealCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String appealId,
            String reason) {}
}
