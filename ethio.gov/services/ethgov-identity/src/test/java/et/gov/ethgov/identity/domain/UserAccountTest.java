package et.gov.ethgov.identity.domain.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserAccountTest {

    private final PasswordHasher hasher = new PasswordHasher() {
        @Override
        public String hash(String plaintext) {
            return "hash:" + plaintext;
        }

        @Override
        public boolean matches(String plaintext, String hash) {
            return hash.equals("hash:" + plaintext);
        }
    };

    @Test
    void registersEmployeeAndAuthenticates() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        TenantMembership membership = new TenantMembership(
                TenantId.generate(), Set.of("CLERK"), Set.of("case:read"));
        UserAccount account = UserAccount.registerPublicEmployee(
                Username.of("clerk@addis.gov.et"),
                "SecurePass1!xyz",
                membership,
                hasher,
                now);

        account.authenticate("SecurePass1!xyz", hasher, now);
        assertThat(account.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.pullDomainEvents()).hasSize(1);
    }

    @Test
    void locksAfterFailedAttempts() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        UserAccount account = UserAccount.registerCitizen(
                Username.of("citizen@example.et"), "SecurePass1!xyz", hasher, now);
        account.pullDomainEvents();

        for (int i = 0; i < PasswordPolicy.MAX_FAILED_ATTEMPTS - 1; i++) {
            assertThatThrownBy(() -> account.authenticate("wrong-password!!", hasher, now))
                    .isInstanceOf(InvalidCredentialsException.class);
        }
        assertThatThrownBy(() -> account.authenticate("wrong-password!!", hasher, now))
                .isInstanceOf(InvalidCredentialsException.class);
        assertThat(account.status()).isEqualTo(AccountStatus.LOCKED);
        assertThat(account.pullDomainEvents()).anyMatch(UserAccountLocked.class::isInstance);
    }

    @Test
    void citizenCannotReceiveTenantMembership() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        UserAccount account = UserAccount.registerCitizen(
                Username.of("citizen@example.et"), "SecurePass1!xyz", hasher, now);
        assertThatThrownBy(() -> account.assignMembership(
                        new TenantMembership(TenantId.generate(), Set.of("CLERK"), Set.of()), now))
                .hasMessageContaining("Citizen");
    }
}
