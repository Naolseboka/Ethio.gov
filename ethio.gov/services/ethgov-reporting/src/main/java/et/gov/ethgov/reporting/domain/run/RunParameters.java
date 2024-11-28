package et.gov.ethgov.reporting.domain.run;

import java.util.Objects;

/** JSON object of runtime parameter values for a report run. */
public final class RunParameters {
    private final String json;

    private RunParameters(String json) {
        this.json = json;
    }

    public static RunParameters of(String raw) {
        Objects.requireNonNull(raw, "parameters");
        String n = raw.trim();
        if (n.length() < 2 || n.length() > 50_000) {
            throw new IllegalArgumentException("parameters length must be between 2 and 50000");
        }
        if (!n.startsWith("{") || !n.endsWith("}")) {
            throw new IllegalArgumentException("parameters must be a JSON object");
        }
        return new RunParameters(n);
    }

    public static RunParameters empty() {
        return new RunParameters("{}");
    }

    public String json() {
        return json;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof RunParameters that && json.equals(that.json));
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
