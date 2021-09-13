package et.gov.ethgov.identity.application.client;

import et.gov.ethgov.identity.application.account.IdentityEventPublisher;
import et.gov.ethgov.identity.domain.account.PasswordHasher;
import et.gov.ethgov.identity.domain.client.OAuthClient;
import et.gov.ethgov.identity.domain.client.OAuthClientId;
import et.gov.ethgov.identity.domain.client.OAuthClientRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Clock;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterOAuthClientUseCase {

    private final OAuthClientRepository clients;
    private final PasswordHasher passwordHasher;
    private final RegisteredClientSynchronizer synchronizer;
    private final Clock clock;

    public RegisterOAuthClientUseCase(
            OAuthClientRepository clients,
            PasswordHasher passwordHasher,
            RegisteredClientSynchronizer synchronizer,
            Clock clock) {
        this.clients = clients;
        this.passwordHasher = passwordHasher;
        this.synchronizer = synchronizer;
        this.clock = clock;
    }

    @Transactional
    public Result execute(Command command) {
        Objects.requireNonNull(command, "command");
        if (clients.existsByClientId(command.clientId())) {
            throw new DomainException(
                    "CLIENT_ID_TAKEN", "Client Id Taken", "OAuth client id already exists", 409);
        }
        String plaintextSecret = command.clientSecret() == null || command.clientSecret().isBlank()
                ? UUID.randomUUID() + "-" + UUID.randomUUID()
                : command.clientSecret();
        if (plaintextSecret.length() < 24) {
            throw new IllegalArgumentException("clientSecret must be at least 24 characters");
        }
        OAuthClient client = OAuthClient.registerConfidential(
                command.clientId(),
                passwordHasher.hash(plaintextSecret),
                TenantId.of(command.tenantId()),
                command.redirectUris(),
                command.grantTypes(),
                command.scopes(),
                clock.instant());
        clients.save(client);
        synchronizer.sync(client, plaintextSecret);
        return new Result(client.id(), client.clientId(), plaintextSecret);
    }

    public record Command(
            String clientId,
            String clientSecret,
            String tenantId,
            Set<String> redirectUris,
            Set<String> grantTypes,
            Set<String> scopes) {}

    public record Result(OAuthClientId id, String clientId, String clientSecret) {}

    public interface RegisteredClientSynchronizer {
        void sync(OAuthClient client, String plaintextSecret);
    }
}
