package et.gov.ethgov.organization.application.budgetcode;

import et.gov.ethgov.organization.domain.budgetcode.Budgetcode;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeId;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateBudgetcodeUseCase {
    private final BudgetcodeRepository repository;
    private final Clock clock;

    public CreateBudgetcodeUseCase(BudgetcodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BudgetcodeId execute(CreateBudgetcodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Budgetcode aggregate = Budgetcode.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateBudgetcodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String budgetcodeId,
            String reason) {}
}
