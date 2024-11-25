package et.gov.ethgov.reporting.domain.run;

import java.util.Objects;
import java.util.UUID;

public final class ReportRunId {
    private final String value;

    private ReportRunId(String value) {
        this.value = value;
    }

    public static ReportRunId generate() {
        return new ReportRunId("rrn_" + UUID.randomUUID());
    }

    public static ReportRunId of(String raw) {
        Objects.requireNonNull(raw, "reportRunId");
        String n = raw.trim();
        if (!n.startsWith("rrn_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid ReportRunId: " + raw);
        }
        return new ReportRunId(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ReportRunId that && value.equals(that.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
