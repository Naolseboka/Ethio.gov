package et.gov.ethgov.identity.domain.account;

import java.util.Optional;

public interface UserAccountRepository {

    boolean existsByUsername(Username username);

    Optional<UserAccount> findById(UserAccountId id);

    Optional<UserAccount> findByUsername(Username username);

    void save(UserAccount account);
}
