package et.gov.ethgov.identity.adapter.in.security;

import et.gov.ethgov.identity.domain.account.AccountStatus;
import et.gov.ethgov.identity.domain.account.UserAccount;
import et.gov.ethgov.identity.domain.account.UserAccountRepository;
import et.gov.ethgov.identity.domain.account.Username;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DomainUserDetailsService implements UserDetailsService {

    private final UserAccountRepository accounts;

    public DomainUserDetailsService(UserAccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = accounts
                .findByUsername(Username.of(username))
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user"));
        if (account.passwordHash() == null) {
            throw new UsernameNotFoundException("Password login not available");
        }
        boolean enabled = account.status() == AccountStatus.ACTIVE;
        boolean locked = account.status() == AccountStatus.LOCKED;
        return User.withUsername(account.username().value())
                .password(account.passwordHash())
                .disabled(!enabled && !locked)
                .accountLocked(locked)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}
