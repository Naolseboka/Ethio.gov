package et.gov.ethgov.land.domain.parcel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class AreaSquareMeters {
    private static final BigDecimal MAX = new BigDecimal("100000000");
    private final BigDecimal value;

    private AreaSquareMeters(BigDecimal value) { this.value = value; }

    public static AreaSquareMeters of(BigDecimal raw) {
        Objects.requireNonNull(raw, "areaSquareMeters");
        BigDecimal n = raw.setScale(2, RoundingMode.HALF_UP);
        if (n.compareTo(BigDecimal.valueOf(0.01)) < 0 || n.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("areaSquareMeters must be between 0.01 and 100000000");
        }
        return new AreaSquareMeters(n);
    }

    public static AreaSquareMeters of(String raw) {
        return of(new BigDecimal(raw));
    }

    public BigDecimal value() { return value; }
    @Override public boolean equals(Object o) {
        return this == o || (o instanceof AreaSquareMeters that && value.compareTo(that.value) == 0);
    }
    @Override public int hashCode() { return value.stripTrailingZeros().hashCode(); }
    @Override public String toString() { return value.toPlainString(); }
}
