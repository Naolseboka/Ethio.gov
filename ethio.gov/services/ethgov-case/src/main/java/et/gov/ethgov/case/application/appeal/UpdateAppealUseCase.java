package et.gov.ethgov.case.application.appeal;

import et.gov.ethgov.case.domain.appeal.Appeal;
import et.gov.ethgov.case.domain.appeal.AppealId;
import et.gov.ethgov.case.domain.appeal.AppealRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateAppealUseCase {
    private final AppealRepository repository;
    private final Clock clock;

    public UpdateAppealUseCase(AppealRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AppealId execute(UpdateAppealCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AppealId id = AppealId.parse(command.appealId());
        Appeal aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("appeal not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateAppealCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String appealId,
            String reason) {}
}
