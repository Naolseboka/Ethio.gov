package et.gov.ethgov.platform.security.auth;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Authenticated principal representation used across EthGov services.
 */
public final class EthGovPrincipal {

    private final String subject;
    private final PrincipalType type;
    private final TenantId tenantId;
    private final Set<String> roles;
    private final Set<String> permissions;

    public EthGovPrincipal(
            String subject,
            PrincipalType type,
            TenantId tenantId,
            Set<String> roles,
            Set<String> permissions) {
        this.subject = Objects.requireNonNull(subject, "subject");
        this.type = Objects.requireNonNull(type, "type");
        this.tenantId = tenantId;
        this.roles = Set.copyOf(roles == null ? Set.of() : roles);
        this.permissions = Set.copyOf(permissions == null ? Set.of() : permissions);
    }

    public String subject() {
        return subject;
    }

    public PrincipalType type() {
        return type;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public Set<String> roles() {
        return Collections.unmodifiableSet(roles);
    }

    public Set<String> permissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public enum PrincipalType {
        CITIZEN,
        PUBLIC_EMPLOYEE,
        SERVICE_ACCOUNT,
        SYSTEM
    }
}
