package et.gov.ethgov.citizen.domain.educationrecord;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EducationrecordCreated. */
public final class EducationrecordCreated {
    private final EducationrecordId id;
    private final Instant occurredAt;

    public EducationrecordCreated(EducationrecordId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public EducationrecordId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
