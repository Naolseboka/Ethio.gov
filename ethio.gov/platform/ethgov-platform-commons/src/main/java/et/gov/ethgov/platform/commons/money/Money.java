package et.gov.ethgov.platform.commons.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Immutable monetary amount in Ethiopian Birr (ETB).
 * Amounts are stored with exactly two decimal places using HALF_EVEN rounding.
 */
public final class Money {

    public static final Currency ETB = Currency.getInstance("ETB");
    private static final int SCALE = 2;

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        if (amount.scale() != SCALE) {
            throw new IllegalArgumentException("Money amount must have scale " + SCALE);
        }
        if (!ETB.equals(currency)) {
            throw new IllegalArgumentException("Only ETB is supported in Phase 0: " + currency);
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static Money etb(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        return new Money(amount.setScale(SCALE, RoundingMode.HALF_EVEN), ETB);
    }

    public static Money etb(String amount) {
        return etb(new BigDecimal(Objects.requireNonNull(amount, "amount")));
    }

    public static Money etb(long majorUnits, int minorUnits) {
        if (minorUnits < 0 || minorUnits > 99) {
            throw new IllegalArgumentException("minorUnits must be between 0 and 99");
        }
        BigDecimal value = BigDecimal.valueOf(majorUnits)
                .add(BigDecimal.valueOf(minorUnits, SCALE));
        return etb(value);
    }

    public static Money zeroEtb() {
        return etb(BigDecimal.ZERO);
    }

    public BigDecimal amount() {
        return amount;
    }

    public Currency currency() {
        return currency;
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return etb(amount.add(other.amount));
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return etb(amount.subtract(other.amount));
    }

    public Money multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "factor");
        return etb(amount.multiply(factor));
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    public int compareTo(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount);
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other");
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch: " + currency + " vs " + other.currency);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Money that)) {
            return false;
        }
        return amount.compareTo(that.amount) == 0 && currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency.getCurrencyCode();
    }
}
