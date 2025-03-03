package et.gov.ethgov.revenue.domain.bill;

import java.util.Objects;
import java.util.UUID;

public final class BillId {
    private final String value;
    private BillId(String value) { this.value = value; }
    public static BillId generate() { return new BillId("rev_" + UUID.randomUUID()); }
    public static BillId of(String raw) {
        Objects.requireNonNull(raw, "billId");
        String n = raw.trim();
        if (!n.startsWith("rev_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid BillId: " + raw);
        }
        return new BillId(n);
    }
    public String value() { return value; }
    @Override public boolean equals(Object o) {
        return this == o || (o instanceof BillId that && value.equals(that.value));
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
