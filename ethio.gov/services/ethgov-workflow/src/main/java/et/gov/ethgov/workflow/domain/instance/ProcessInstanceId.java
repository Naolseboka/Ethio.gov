package et.gov.ethgov.workflow.domain.instance;

import java.util.Objects;
import java.util.UUID;

public final class ProcessInstanceId {

    private final String value;

    private ProcessInstanceId(String value) {
        this.value = value;
    }

    public static ProcessInstanceId generate() {
        return new ProcessInstanceId("wpi_" + UUID.randomUUID());
    }

    public static ProcessInstanceId of(String raw) {
        Objects.requireNonNull(raw, "processInstanceId");
        String normalized = raw.trim();
        if (!normalized.startsWith("wpi_") || normalized.length() < 40) {
            throw new IllegalArgumentException("Invalid ProcessInstanceId: " + raw);
        }
        return new ProcessInstanceId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProcessInstanceId that)) {
            return false;
        }
        return value.equals(that.value);
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
