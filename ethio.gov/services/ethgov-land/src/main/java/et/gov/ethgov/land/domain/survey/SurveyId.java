package et.gov.ethgov.land.domain.survey;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Survey. */
public final class SurveyId {
    private final UUID value;

    private SurveyId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SurveyId newId() {
        return new SurveyId(UUID.randomUUID());
    }

    public static SurveyId of(UUID value) {
        return new SurveyId(value);
    }

    public static SurveyId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SurveyId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SurveyId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
