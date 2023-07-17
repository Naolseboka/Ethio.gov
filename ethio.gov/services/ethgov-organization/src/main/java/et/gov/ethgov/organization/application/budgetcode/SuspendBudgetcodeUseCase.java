package et.gov.ethgov.organization.application.budgetcode;

import et.gov.ethgov.organization.domain.budgetcode.Budgetcode;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeId;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendBudgetcodeUseCase {
    private final BudgetcodeRepository repository;
    private final Clock clock;

    public SuspendBudgetcodeUseCase(BudgetcodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public BudgetcodeId execute(SuspendBudgetcodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        BudgetcodeId id = BudgetcodeId.parse(command.budgetcodeId());
        Budgetcode aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("budgetcode not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendBudgetcodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String budgetcodeId,
            String reason) {}
}
