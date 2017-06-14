package et.gov.ethgov.platform.security.auth;

import java.util.Objects;
import java.util.Optional;

/**
 * Thread-bound security principal for application services.
 */
public final class SecurityContext {

    private static final ThreadLocal<EthGovPrincipal> CURRENT = new ThreadLocal<>();

    private SecurityContext() {}

    public static void set(EthGovPrincipal principal) {
        CURRENT.set(Objects.requireNonNull(principal, "principal"));
    }

    public static Optional<EthGovPrincipal> current() {
        return Optional.ofNullable(CURRENT.get());
    }

    public static EthGovPrincipal require() {
        EthGovPrincipal principal = CURRENT.get();
        if (principal == null) {
            throw new IllegalStateException("No authenticated principal bound");
        }
        return principal;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
