package et.gov.ethgov.identity.domain.passwordpolicy;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PasswordpolicyCreated. */
public final class PasswordpolicyCreated {
    private final PasswordpolicyId id;
    private final Instant occurredAt;

    public PasswordpolicyCreated(PasswordpolicyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PasswordpolicyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
