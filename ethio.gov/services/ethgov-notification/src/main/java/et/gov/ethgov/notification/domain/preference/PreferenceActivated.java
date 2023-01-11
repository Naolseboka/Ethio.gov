package et.gov.ethgov.notification.domain.preference;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PreferenceActivated. */
public final class PreferenceActivated {
    private final PreferenceId id;
    private final Instant occurredAt;

    public PreferenceActivated(PreferenceId id, Instant occurredAt) {
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
