package et.gov.ethgov.platform.security.abac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.security.auth.EthGovPrincipal;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AbacPolicyEngineTest {

    private final AbacPolicyEngine engine = new AbacPolicyEngine();

    @Test
    void deniesCrossTenantAccess() {
        TenantId tenantA = TenantId.generate();
        TenantId tenantB = TenantId.generate();
        EthGovPrincipal principal = new EthGovPrincipal(
                "emp-1",
                EthGovPrincipal.PrincipalType.PUBLIC_EMPLOYEE,
                tenantA,
                Set.of("CLERK"),
                Set.of("case:read"));

        AbacPolicyEngine.AccessDecision decision =
                engine.decide(principal, new AbacPolicyEngine.AccessRequest("case:read", tenantB));

        assertThat(decision.permitted()).isFalse();
        assertThatThrownBy(decision::requirePermitted).isInstanceOf(SecurityException.class);
    }

    @Test
    void permitsMatchingPermissionAndTenant() {
        TenantId tenantId = TenantId.generate();
        EthGovPrincipal principal = new EthGovPrincipal(
                "emp-1",
                EthGovPrincipal.PrincipalType.PUBLIC_EMPLOYEE,
                tenantId,
                Set.of("CLERK"),
                Set.of("case:read"));

        AbacPolicyEngine.AccessDecision decision =
                engine.decide(principal, new AbacPolicyEngine.AccessRequest("case:read", tenantId));

        assertThat(decision.permitted()).isTrue();
    }
}
