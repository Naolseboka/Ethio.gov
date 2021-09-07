package et.gov.ethgov.identity.application.account;

import et.gov.ethgov.identity.domain.account.PasswordHasher;
import et.gov.ethgov.identity.domain.account.UserAccount;
import et.gov.ethgov.identity.domain.account.UserAccountId;
import et.gov.ethgov.identity.domain.account.UserAccountRepository;
import et.gov.ethgov.identity.domain.account.Username;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterCitizenUseCase {

    private final UserAccountRepository accounts;
    private final PasswordHasher passwordHasher;
    private final IdentityEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterCitizenUseCase(
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
        UserAccount account = UserAccount.registerCitizen(
                username, command.password(), passwordHasher, clock.instant());
        accounts.save(account);
        eventPublisher.publishAll(account.pullDomainEvents());
        return account.id();
    }

    public record Command(String username, String password) {}
}
