package et.gov.ethgov.notification.domain.preference;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PreferenceUpdated. */
public final class PreferenceUpdated {
    private final PreferenceId id;
    private final Instant occurredAt;

    public PreferenceUpdated(PreferenceId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PreferenceId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
