package et.gov.ethgov.platform.security.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class JwtPrincipalMapperTest {

    private final JwtPrincipalMapper mapper = new JwtPrincipalMapper();

    @AfterEach
    void clear() {
        SecurityContext.clear();
    }

    @Test
    void mapsClaimsToPrincipal() {
        TenantId tenantId = TenantId.generate();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("emp-42")
                .claim(JwtPrincipalMapper.CLAIM_PRINCIPAL_TYPE, "PUBLIC_EMPLOYEE")
                .claim(JwtPrincipalMapper.CLAIM_TENANT_ID, tenantId.value())
                .claim(JwtPrincipalMapper.CLAIM_ROLES, List.of("CLERK"))
                .claim(JwtPrincipalMapper.CLAIM_PERMISSIONS, List.of("case:read", "case:write"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        EthGovPrincipal principal = mapper.map(jwt);
        assertThat(principal.subject()).isEqualTo("emp-42");
        assertThat(principal.tenantId()).isEqualTo(tenantId);
        assertThat(principal.hasPermission("case:read")).isTrue();
        assertThat(principal.hasRole("CLERK")).isTrue();

        SecurityContext.set(principal);
        assertThat(SecurityContext.require().subject()).isEqualTo("emp-42");
        SecurityContext.clear();
        assertThatThrownBy(SecurityContext::require).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejectsMissingPrincipalType() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("emp-42")
                .claims(claims -> claims.putAll(Map.of()))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        assertThatThrownBy(() -> mapper.map(jwt)).isInstanceOf(IllegalArgumentException.class);
    }
}
