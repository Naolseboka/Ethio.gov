package et.gov.ethgov.identity.application.account;

import et.gov.ethgov.identity.domain.account.PasswordHasher;
import et.gov.ethgov.identity.domain.account.TenantMembership;
import et.gov.ethgov.identity.domain.account.UserAccount;
import et.gov.ethgov.identity.domain.account.UserAccountId;
import et.gov.ethgov.identity.domain.account.UserAccountRepository;
import et.gov.ethgov.identity.domain.account.Username;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterPublicEmployeeUseCase {

    private final UserAccountRepository accounts;
    private final PasswordHasher passwordHasher;
    private final IdentityEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterPublicEmployeeUseCase(
            UserAccountRepository accounts,
            PasswordHasher passwordHasher,
            IdentityEventPublisher eventPublisher,
            Clock clock) {
        this.accounts = accounts;
        this.passwordHasher = passwordHasher;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public UserAccountId execute(Command command) {
        Objects.requireNonNull(command, "command");
        Username username = Username.of(command.username());
        if (accounts.existsByUsername(username)) {
            throw new DomainException(
                    "USERNAME_TAKEN", "Username Taken", "Username already registered", 409);
        }
        TenantMembership membership = new TenantMembership(
                TenantId.of(command.tenantId()), command.roles(), command.permissions());
        Instant now = clock.instant();
        UserAccount account = UserAccount.registerPublicEmployee(
                username, command.password(), membership, passwordHasher, now);
        accounts.save(account);
        eventPublisher.publishAll(account.pullDomainEvents());
        return account.id();
    }

    public record Command(
            String username,
            String password,
            String tenantId,
            Set<String> roles,
            Set<String> permissions) {}
}
