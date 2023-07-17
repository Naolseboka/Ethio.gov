package et.gov.ethgov.organization.application.budgetcode;

import et.gov.ethgov.organization.domain.budgetcode.Budgetcode;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeId;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateBudgetcodeUseCase {
    private final BudgetcodeRepository repository;
    private final Clock clock;

    public UpdateBudgetcodeUseCase(BudgetcodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BudgetcodeId execute(UpdateBudgetcodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BudgetcodeId id = BudgetcodeId.parse(command.budgetcodeId());
        Budgetcode aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("budgetcode not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateBudgetcodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String budgetcodeId,
            String reason) {}
}
