package et.gov.ethgov.case.application.appeal;

import et.gov.ethgov.case.domain.appeal.Appeal;
import et.gov.ethgov.case.domain.appeal.AppealId;
import et.gov.ethgov.case.domain.appeal.AppealRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAppealUseCase {
    private final AppealRepository repository;
    private final Clock clock;

    public CreateAppealUseCase(AppealRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AppealId execute(CreateAppealCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Appeal aggregate = Appeal.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAppealCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String appealId,
            String reason) {}
}
