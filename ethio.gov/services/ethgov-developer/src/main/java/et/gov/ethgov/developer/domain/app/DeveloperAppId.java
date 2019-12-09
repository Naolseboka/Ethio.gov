package et.gov.ethgov.developer.domain.app;

import java.util.Objects;
import java.util.UUID;

public final class DeveloperAppId {
    private final String value;

    private DeveloperAppId(String value) {
        this.value = value;
    }

    public static DeveloperAppId generate() {
        return new DeveloperAppId("dap_" + UUID.randomUUID());
    }

    public static DeveloperAppId of(String raw) {
        Objects.requireNonNull(raw, "developerAppId");
        String n = raw.trim();
        if (!n.startsWith("dap_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid DeveloperAppId: " + raw);
        }
        return new DeveloperAppId(n);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof DeveloperAppId that && value.equals(that.value));
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
