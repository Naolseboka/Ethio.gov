package et.gov.ethgov.organization.domain.budgetcode;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BudgetcodeCreated. */
public final class BudgetcodeCreated {
    private final BudgetcodeId id;
    private final Instant occurredAt;

    public BudgetcodeCreated(BudgetcodeId id, Instant occurredAt) {
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
