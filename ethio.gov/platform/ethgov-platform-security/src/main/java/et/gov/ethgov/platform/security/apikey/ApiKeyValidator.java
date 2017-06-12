package et.gov.ethgov.platform.security.apikey;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Shared platform utility: ApiKeyValidator for cross-service security concerns. */
public final class ApiKeyValidator {
    private final Map<String, Instant> state = new ConcurrentHashMap<>();

    public String begin(String key) {
        Objects.requireNonNull(key, "key");
        String token = UUID.randomUUID().toString();
        state.put(key + ":" + token, Instant.now());
        return token;
    }

    public boolean complete(String key, String token) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(token, "token");
        return state.remove(key + ":" + token) != null;
    }

    public Optional<Instant> startedAt(String key, String token) {
        return Optional.ofNullable(state.get(key + ":" + token));
    }

    public int size() {
        return state.size();
    }

    public void clear() {
        state.clear();
    }
}
