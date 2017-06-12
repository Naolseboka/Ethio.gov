package et.gov.ethgov.platform.security.abac;

import et.gov.ethgov.platform.security.auth.EthGovPrincipal;
import java.util.Objects;

/**
 * Attribute-based access decision helper used by application services.
 */
public final class AbacPolicyEngine {

    public AccessDecision decide(EthGovPrincipal principal, AccessRequest request) {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(request, "request");

        if (principal.type() == EthGovPrincipal.PrincipalType.SYSTEM) {
            return AccessDecision.permit("system principal");
        }

        if (request.requiredPermission() != null
                && !principal.hasPermission(request.requiredPermission())
                && !principal.hasRole("TENANT_ADMIN")) {
            return AccessDecision.deny("missing permission " + request.requiredPermission());
        }

        if (request.resourceTenantId() != null) {
            if (principal.tenantId() == null) {
                return AccessDecision.deny("principal has no tenant binding");
            }
            if (!principal.tenantId().equals(request.resourceTenantId())) {
                return AccessDecision.deny("tenant isolation violation");
            }
        }

        return AccessDecision.permit("policy satisfied");
    }

    public record AccessRequest(String requiredPermission, et.gov.ethgov.platform.commons.tenant.TenantId resourceTenantId) {}

    public record AccessDecision(boolean permitted, String reason) {
        public static AccessDecision permit(String reason) {
            return new AccessDecision(true, reason);
        }

        public static AccessDecision deny(String reason) {
            return new AccessDecision(false, reason);
        }

        public void requirePermitted() {
            if (!permitted) {
                throw new SecurityException("Access denied: " + reason);
            }
        }
    }
}
