package et.gov.ethgov.organization.domain.budgetcode;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BudgetcodeActivated. */
public final class BudgetcodeActivated {
    private final BudgetcodeId id;
    private final Instant occurredAt;

    public BudgetcodeActivated(BudgetcodeId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public BudgetcodeId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
