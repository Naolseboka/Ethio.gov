package et.gov.ethgov.land.domain.cadastralmap;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CadastralmapClosed. */
public final class CadastralmapClosed {
    private final CadastralmapId id;
    private final Instant occurredAt;

    public CadastralmapClosed(CadastralmapId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CadastralmapId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
