package et.gov.ethgov.organization.domain.budgetcode;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Budgetcode. */
public final class BudgetcodeId {
    private final UUID value;

    private BudgetcodeId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static BudgetcodeId newId() {
        return new BudgetcodeId(UUID.randomUUID());
    }

    public static BudgetcodeId of(UUID value) {
        return new BudgetcodeId(value);
    }

    public static BudgetcodeId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new BudgetcodeId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BudgetcodeId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
