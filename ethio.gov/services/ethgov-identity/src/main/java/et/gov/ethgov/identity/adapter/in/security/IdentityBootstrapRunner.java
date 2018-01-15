package et.gov.ethgov.identity.adapter.in.security;

import et.gov.ethgov.identity.application.account.RegisterPublicEmployeeUseCase;
import et.gov.ethgov.identity.application.client.RegisterOAuthClientUseCase;
import et.gov.ethgov.identity.domain.account.UserAccountRepository;
import et.gov.ethgov.identity.domain.account.Username;
import et.gov.ethgov.identity.domain.client.OAuthClientRepository;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Creates the initial tenant admin and admin OAuth client when the identity database is empty.
 */
@Component
public class IdentityBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(IdentityBootstrapRunner.class);

    private final boolean enabled;
    private final String adminUsername;
    private final String adminPassword;
    private final String adminTenantId;
    private final String adminClientId;
    private final String adminClientSecret;
    private final UserAccountRepository accounts;
    private final OAuthClientRepository clients;
    private final RegisterPublicEmployeeUseCase registerPublicEmployee;
    private final RegisterOAuthClientUseCase registerOAuthClient;

    public IdentityBootstrapRunner(
            @Value("${ethgov.identity.bootstrap.enabled:false}") boolean enabled,
            @Value("${ethgov.identity.bootstrap.admin-username:}") String adminUsername,
            @Value("${ethgov.identity.bootstrap.admin-password:}") String adminPassword,
            @Value("${ethgov.identity.bootstrap.admin-tenant-id:}") String adminTenantId,
            @Value("${ethgov.identity.bootstrap.admin-client-id:ethgov-identity-admin}") String adminClientId,
            @Value("${ethgov.identity.bootstrap.admin-client-secret:}") String adminClientSecret,
            UserAccountRepository accounts,
            OAuthClientRepository clients,
            RegisterPublicEmployeeUseCase registerPublicEmployee,
            RegisterOAuthClientUseCase registerOAuthClient) {
        this.enabled = enabled;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminTenantId = adminTenantId;
        this.adminClientId = adminClientId;
        this.adminClientSecret = adminClientSecret;
        this.accounts = accounts;
        this.clients = clients;
        this.registerPublicEmployee = registerPublicEmployee;
        this.registerOAuthClient = registerOAuthClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        if (adminUsername.isBlank() || adminPassword.isBlank() || adminTenantId.isBlank()) {
            throw new IllegalStateException("Bootstrap enabled but admin credentials are incomplete");
        }
        if (!accounts.existsByUsername(Username.of(adminUsername))) {
            registerPublicEmployee.execute(new RegisterPublicEmployeeUseCase.Command(
                    adminUsername,
                    adminPassword,
                    adminTenantId,
                    Set.of("TENANT_ADMIN"),
                    Set.of("identity.admin", "identity.user.read", "identity.user.write")));
            log.info("Bootstrapped identity admin user {}", adminUsername);
        }
        if (!clients.existsByClientId(adminClientId)) {
            String secret = adminClientSecret.isBlank()
                    ? "ChangeMe-IdentityAdmin-Secret-001!"
                    : adminClientSecret;
            registerOAuthClient.execute(new RegisterOAuthClientUseCase.Command(
                    adminClientId,
                    secret,
                    adminTenantId,
                    Set.of("http://localhost:8081/login/oauth2/code/ethgov-identity-admin"),
                    Set.of("client_credentials", "authorization_code", "refresh_token"),
                    Set.of("openid", "identity.admin")));
            log.info("Bootstrapped identity admin OAuth client {}", adminClientId);
        }
    }
}
