package et.gov.ethgov.reporting.domain.definition;

import java.util.Objects;
import java.util.UUID;

public final class ReportDefinitionId {
    private final String value;

    private ReportDefinitionId(String value) {
        this.value = value;
    }

    public static ReportDefinitionId generate() {
        return new ReportDefinitionId("rpt_" + UUID.randomUUID());
    }

    public static ReportDefinitionId of(String raw) {
        Objects.requireNonNull(raw, "reportDefinitionId");
        String n = raw.trim();
        if (!n.startsWith("rpt_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid ReportDefinitionId: " + raw);
        }
        return new ReportDefinitionId(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ReportDefinitionId that && value.equals(that.value));
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
