package et.gov.ethgov.identity.adapter.in.security;

import et.gov.ethgov.identity.domain.account.AccountStatus;
import et.gov.ethgov.identity.domain.account.AccountType;
import et.gov.ethgov.identity.domain.account.TenantMembership;
import et.gov.ethgov.identity.domain.account.UserAccount;
import et.gov.ethgov.identity.domain.account.UserAccountRepository;
import et.gov.ethgov.identity.domain.account.Username;
import et.gov.ethgov.platform.security.auth.JwtPrincipalMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class EthGovTokenClaimsService {

    private final UserAccountRepository accounts;

    public EthGovTokenClaimsService(UserAccountRepository accounts) {
        this.accounts = accounts;
    }

    public Map<String, Object> claimsForUsername(String username) {
        Optional<UserAccount> account = accounts.findByUsername(Username.of(username));
        if (account.isEmpty() || account.get().status() != AccountStatus.ACTIVE) {
            return Map.of();
        }
        UserAccount user = account.get();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtPrincipalMapper.CLAIM_PRINCIPAL_TYPE, toPrincipalType(user.type()));
        List<TenantMembership> memberships = user.memberships();
        if (!memberships.isEmpty()) {
            TenantMembership primary = memberships.getFirst();
            claims.put(JwtPrincipalMapper.CLAIM_TENANT_ID, primary.tenantId().value());
            claims.put(JwtPrincipalMapper.CLAIM_ROLES, List.copyOf(primary.roles()));
            claims.put(JwtPrincipalMapper.CLAIM_PERMISSIONS, List.copyOf(primary.permissions()));
        } else {
            claims.put(JwtPrincipalMapper.CLAIM_ROLES, List.of());
            claims.put(JwtPrincipalMapper.CLAIM_PERMISSIONS, List.of());
        }
        return claims;
    }

    private static String toPrincipalType(AccountType type) {
        return switch (type) {
            case CITIZEN -> "CITIZEN";
            case PUBLIC_EMPLOYEE -> "PUBLIC_EMPLOYEE";
            case SERVICE_ACCOUNT -> "SERVICE_ACCOUNT";
        };
    }
}
