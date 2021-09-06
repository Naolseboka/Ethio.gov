package et.gov.ethgov.identity.application.account;

import et.gov.ethgov.identity.domain.account.TenantMembership;
import et.gov.ethgov.identity.domain.account.UserAccount;
import et.gov.ethgov.identity.domain.account.UserAccountId;
import et.gov.ethgov.identity.domain.account.UserAccountRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Clock;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignTenantMembershipUseCase {

    private final UserAccountRepository accounts;
    private final IdentityEventPublisher eventPublisher;
    private final Clock clock;

    public AssignTenantMembershipUseCase(
            UserAccountRepository accounts, IdentityEventPublisher eventPublisher, Clock clock) {
        this.accounts = accounts;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        UserAccount account = accounts
                .findById(UserAccountId.of(command.accountId()))
                .orElseThrow(() -> new DomainException(
                        "ACCOUNT_NOT_FOUND", "Account Not Found", "Account does not exist", 404));
        TenantMembership membership = new TenantMembership(
                TenantId.of(command.tenantId()), command.roles(), command.permissions());
        account.assignMembership(membership, clock.instant());
        accounts.save(account);
        eventPublisher.publishAll(account.pullDomainEvents());
    }

    public record Command(
            String accountId, String tenantId, Set<String> roles, Set<String> permissions) {}
}
