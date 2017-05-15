package et.gov.ethgov.platform.commons.tenant;

import java.util.Objects;
import java.util.Optional;

/**
 * Request-scoped tenant context propagated across service boundaries via headers and MDC.
 */
public final class TenantContext {

    public static final String HEADER_NAME = "X-EthGov-Tenant-Id";

    private static final ThreadLocal<TenantId> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(TenantId tenantId) {
        CURRENT.set(Objects.requireNonNull(tenantId, "tenantId"));
    }

    public static Optional<TenantId> current() {
        return Optional.ofNullable(CURRENT.get());
    }

    public static TenantId require() {
        TenantId tenantId = CURRENT.get();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant is bound to the current thread");
        }
        return tenantId;
    }

    public static void clear() {
        CURRENT.remove();
    }

    public static <T> T callWith(TenantId tenantId, TenantCallable<T> callable) {
        TenantId previous = CURRENT.get();
        set(tenantId);
        try {
            return callable.call();
        } finally {
            if (previous == null) {
                clear();
            } else {
                set(previous);
            }
        }
    }

    @FunctionalInterface
    public interface TenantCallable<T> {
        T call();
    }
}
