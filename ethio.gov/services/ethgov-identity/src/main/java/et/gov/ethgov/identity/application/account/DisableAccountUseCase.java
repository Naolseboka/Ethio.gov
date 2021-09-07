package et.gov.ethgov.identity.application.account;

import et.gov.ethgov.identity.domain.account.UserAccount;
import et.gov.ethgov.identity.domain.account.UserAccountId;
import et.gov.ethgov.identity.domain.account.UserAccountRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisableAccountUseCase {

    private final UserAccountRepository accounts;
    private final IdentityEventPublisher eventPublisher;
    private final Clock clock;

    public DisableAccountUseCase(
            UserAccountRepository accounts, IdentityEventPublisher eventPublisher, Clock clock) {
        this.accounts = accounts;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String accountId) {
        Objects.requireNonNull(accountId, "accountId");
        UserAccount account = accounts
                .findById(UserAccountId.of(accountId))
                .orElseThrow(() -> new DomainException(
                        "ACCOUNT_NOT_FOUND", "Account Not Found", "Account does not exist", 404));
        account.disable(clock.instant());
        accounts.save(account);
        eventPublisher.publishAll(account.pullDomainEvents());
    }
}
