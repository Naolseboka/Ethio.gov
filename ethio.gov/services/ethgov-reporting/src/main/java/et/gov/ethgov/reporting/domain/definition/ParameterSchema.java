package et.gov.ethgov.reporting.domain.definition;

import java.util.Objects;

/** JSON object describing accepted report parameters (schema, not values). */
public final class ParameterSchema {
    private final String json;

    private ParameterSchema(String json) {
        this.json = json;
    }

    public static ParameterSchema of(String raw) {
        Objects.requireNonNull(raw, "parameterSchema");
        String n = raw.trim();
        if (n.length() < 2 || n.length() > 20_000) {
            throw new IllegalArgumentException("parameterSchema length must be between 2 and 20000");
        }
        if (!n.startsWith("{") || !n.endsWith("}")) {
            throw new IllegalArgumentException("parameterSchema must be a JSON object");
        }
        return new ParameterSchema(n);
    }

    public String json() {
        return json;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ParameterSchema that && json.equals(that.json));
    }

    @Override
    public int hashCode() {
        return json.hashCode();
    }

    @Override
    public String toString() {
        return json;
    }
}
