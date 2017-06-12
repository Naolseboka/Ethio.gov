package et.gov.ethgov.platform.security.auth;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Maps EthGov JWT claims into {@link EthGovPrincipal}.
 *
 * <p>Claim contract (Phase 0 baseline, finalized in Identity Platform):
 * <ul>
 *   <li>{@code sub} — subject</li>
 *   <li>{@code ethgov_principal_type} — CITIZEN | PUBLIC_EMPLOYEE | SERVICE_ACCOUNT | SYSTEM</li>
 *   <li>{@code ethgov_tenant_id} — optional tenant binding</li>
 *   <li>{@code ethgov_roles} — string array</li>
 *   <li>{@code ethgov_permissions} — string array</li>
 * </ul>
 */
public final class JwtPrincipalMapper {

    public static final String CLAIM_PRINCIPAL_TYPE = "ethgov_principal_type";
    public static final String CLAIM_TENANT_ID = "ethgov_tenant_id";
    public static final String CLAIM_ROLES = "ethgov_roles";
    public static final String CLAIM_PERMISSIONS = "ethgov_permissions";

    public EthGovPrincipal map(Jwt jwt) {
        Objects.requireNonNull(jwt, "jwt");
        String subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("JWT missing subject");
        }
        EthGovPrincipal.PrincipalType type = EthGovPrincipal.PrincipalType.valueOf(
                requireClaim(jwt, CLAIM_PRINCIPAL_TYPE));
        TenantId tenantId = null;
        String tenantClaim = jwt.getClaimAsString(CLAIM_TENANT_ID);
        if (tenantClaim != null && !tenantClaim.isBlank()) {
            tenantId = TenantId.of(tenantClaim);
        }
        return new EthGovPrincipal(subject, type, tenantId, claimSet(jwt, CLAIM_ROLES), claimSet(jwt, CLAIM_PERMISSIONS));
    }

    private static String requireClaim(Jwt jwt, String name) {
        String value = jwt.getClaimAsString(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("JWT missing claim: " + name);
        }
        return value;
    }

    private static Set<String> claimSet(Jwt jwt, String name) {
        Object raw = jwt.getClaim(name);
        if (raw == null) {
            return Set.of();
        }
        if (raw instanceof List<?> list) {
            Set<String> values = new LinkedHashSet<>();
            for (Object item : list) {
                if (item != null) {
                    values.add(item.toString());
                }
            }
            return values;
        }
        if (raw instanceof String[] array) {
            return new LinkedHashSet<>(Arrays.asList(array));
        }
        if (raw instanceof String csv) {
            if (csv.isBlank()) {
                return Set.of();
            }
            return new LinkedHashSet<>(Arrays.asList(csv.split(",")));
        }
        throw new IllegalArgumentException("Unsupported claim type for " + name + ": " + raw.getClass());
    }
}
