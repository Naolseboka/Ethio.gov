package et.gov.ethgov.identity.domain.device;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DeviceCreated. */
public final class DeviceCreated {
    private final DeviceId id;
    private final Instant occurredAt;

    public DeviceCreated(DeviceId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DeviceId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
