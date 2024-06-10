package et.gov.ethgov.permits.domain.permit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class FloorAreaSquareMeters {
    private static final BigDecimal MAX = new BigDecimal("10000000");
    private final BigDecimal value;

    private FloorAreaSquareMeters(BigDecimal value) { this.value = value; }

    public static FloorAreaSquareMeters of(BigDecimal raw) {
        Objects.requireNonNull(raw, "floorAreaSquareMeters");
        BigDecimal n = raw.setScale(2, RoundingMode.HALF_UP);
        if (n.compareTo(BigDecimal.valueOf(0.01)) < 0 || n.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("floorAreaSquareMeters must be between 0.01 and 10000000");
        }
        return new FloorAreaSquareMeters(n);
    }

    public static FloorAreaSquareMeters of(String raw) {
        return of(new BigDecimal(raw));
    }

    public BigDecimal value() { return value; }
    @Override public boolean equals(Object o) {
        return this == o || (o instanceof FloorAreaSquareMeters that && value.compareTo(that.value) == 0);
    }
    @Override public int hashCode() { return value.stripTrailingZeros().hashCode(); }
    @Override public String toString() { return value.toPlainString(); }
}
