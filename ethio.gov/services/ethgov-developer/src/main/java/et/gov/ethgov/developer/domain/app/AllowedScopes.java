package et.gov.ethgov.developer.domain.app;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** JSON-array-shaped allow-list of OAuth/API scopes, e.g. ["citizen.read","opendata.read"]. */
public final class AllowedScopes {
    private final String json;

    private AllowedScopes(String json) {
        this.json = json;
    }

    public static AllowedScopes of(String raw) {
        Objects.requireNonNull(raw, "allowedScopes");
        String n = raw.trim();
        if (!n.startsWith("[") || !n.endsWith("]")) {
            throw new IllegalArgumentException("allowedScopes must be a JSON array");
        }
        if (n.length() < 2 || n.length() > 4000) {
            throw new IllegalArgumentException("allowedScopes length must be between 2 and 4000");
        }
        return new AllowedScopes(n);
    }

    public static AllowedScopes ofSet(Set<String> scopes) {
        Objects.requireNonNull(scopes, "scopes");
        if (scopes.isEmpty()) {
            throw new IllegalArgumentException("at least one scope is required");
        }
        for (String scope : scopes) {
            if (scope == null || scope.isBlank() || scope.length() > 80 || scope.contains(" ")) {
                throw new IllegalArgumentException("invalid scope: " + scope);
            }
        }
        String json = scopes.stream()
                .map(String::trim)
                .sorted()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(",", "[", "]"));
        return new AllowedScopes(json);
    }

    public String json() {
        return json;
    }

    public Set<String> asSet() {
        String inner = json.substring(1, json.length() - 1).trim();
        if (inner.isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(inner.split(","))
                .map(String::trim)
                .map(s -> s.replaceAll("^\"|\"$", ""))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof AllowedScopes that && json.equals(that.json));
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
