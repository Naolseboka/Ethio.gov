package et.gov.ethgov.directory.domain.counter;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CounterActivated. */
public final class CounterActivated {
    private final CounterId id;
    private final Instant occurredAt;

    public CounterActivated(CounterId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CounterId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
