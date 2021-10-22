package et.gov.ethgov.identity.domain.account;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Binding of an account to a government tenant with RBAC roles and fine-grained permissions.
 */
public final class TenantMembership {

    private final TenantId tenantId;
    private final Set<String> roles;
    private final Set<String> permissions;

    public TenantMembership(TenantId tenantId, Set<String> roles, Set<String> permissions) {
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.roles = normalize(roles, "roles");
        this.permissions = normalize(permissions, "permissions");
        if (this.roles.isEmpty() && this.permissions.isEmpty()) {
            throw new IllegalArgumentException("membership requires at least one role or permission");
        }
    }

    private static Set<String> normalize(Set<String> values, String field) {
        if (values == null || values.isEmpty()) {
            return Set.of();
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String value : values) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(field + " contains blank value");
            }
            String item = value.trim();
            if (item.length() > 128) {
                throw new IllegalArgumentException(field + " value too long");
            }
            normalized.add(item);
        }
        return Collections.unmodifiableSet(normalized);
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public Set<String> roles() {
        return roles;
    }

    public Set<String> permissions() {
        return permissions;
    }

    public TenantMembership withAddedRole(String role) {
        Set<String> next = new LinkedHashSet<>(roles);
        next.add(Objects.requireNonNull(role, "role").trim());
        return new TenantMembership(tenantId, next, permissions);
    }

    public TenantMembership withAddedPermission(String permission) {
        Set<String> next = new LinkedHashSet<>(permissions);
        next.add(Objects.requireNonNull(permission, "permission").trim());
        return new TenantMembership(tenantId, roles, next);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TenantMembership that)) {
            return false;
        }
        return tenantId.equals(that.tenantId);
    }

    @Override
    public int hashCode() {
        return tenantId.hashCode();
    }
}
