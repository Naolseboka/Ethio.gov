package et.gov.ethgov.identity.domain.account;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregate root for authentication identity.
 */
public final class UserAccount {

    private final UserAccountId id;
    private final Username username;
    private final AccountType type;
    private AccountStatus status;
    private String passwordHash;
    private final Map<String, TenantMembership> membershipsByTenant;
    private int failedLoginAttempts;
    private Instant lockedUntil;
    private final Instant createdAt;
    private Instant updatedAt;
    private long version;
    private final List<IdentityDomainEvent> domainEvents = new ArrayList<>();

    private UserAccount(
            UserAccountId id,
            Username username,
            AccountType type,
            AccountStatus status,
            String passwordHash,
            Map<String, TenantMembership> membershipsByTenant,
            int failedLoginAttempts,
            Instant lockedUntil,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.status = status;
        this.passwordHash = passwordHash;
        this.membershipsByTenant = membershipsByTenant;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lockedUntil = lockedUntil;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static UserAccount registerPublicEmployee(
            Username username,
            String plaintextPassword,
            TenantMembership primaryMembership,
            PasswordHasher hasher,
            Instant now) {
        Objects.requireNonNull(primaryMembership, "primaryMembership");
        PasswordPolicy.validatePlaintext(plaintextPassword);
        UserAccount account = new UserAccount(
                UserAccountId.generate(),
                username,
                AccountType.PUBLIC_EMPLOYEE,
                AccountStatus.ACTIVE,
                hasher.hash(plaintextPassword),
                new LinkedHashMap<>(),
                0,
                null,
                now,
                now,
                0);
        account.membershipsByTenant.put(primaryMembership.tenantId().value(), primaryMembership);
        account.record(new UserAccountRegistered(account.id, account.username, account.type, now));
        return account;
    }

    public static UserAccount registerCitizen(
            Username username, String plaintextPassword, PasswordHasher hasher, Instant now) {
        PasswordPolicy.validatePlaintext(plaintextPassword);
        UserAccount account = new UserAccount(
                UserAccountId.generate(),
                username,
                AccountType.CITIZEN,
                AccountStatus.ACTIVE,
                hasher.hash(plaintextPassword),
                new LinkedHashMap<>(),
                0,
                null,
                now,
                now,
                0);
        account.record(new UserAccountRegistered(account.id, account.username, account.type, now));
        return account;
    }

    public static UserAccount registerServiceAccount(
            Username username, TenantMembership membership, Instant now) {
        if (!username.value().startsWith("svc.")) {
            throw new IllegalArgumentException("service accounts must use svc.* username");
        }
        Objects.requireNonNull(membership, "membership");
        UserAccount account = new UserAccount(
                UserAccountId.generate(),
                username,
                AccountType.SERVICE_ACCOUNT,
                AccountStatus.ACTIVE,
                null,
                new LinkedHashMap<>(),
                0,
                null,
                now,
                now,
                0);
        account.membershipsByTenant.put(membership.tenantId().value(), membership);
        account.record(new UserAccountRegistered(account.id, account.username, account.type, now));
        return account;
    }

    public static UserAccount rehydrate(
            UserAccountId id,
            Username username,
            AccountType type,
            AccountStatus status,
            String passwordHash,
            List<TenantMembership> memberships,
            int failedLoginAttempts,
            Instant lockedUntil,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        Map<String, TenantMembership> map = new LinkedHashMap<>();
        for (TenantMembership membership : memberships) {
            map.put(membership.tenantId().value(), membership);
        }
        return new UserAccount(
                id,
                username,
                type,
                status,
                passwordHash,
                map,
                failedLoginAttempts,
                lockedUntil,
                createdAt,
                updatedAt,
                version);
    }

    public void authenticate(String plaintextPassword, PasswordHasher hasher, Instant now) {
        ensureInteractive();
        ensureCanAuthenticate(now);
        if (passwordHash == null || !hasher.matches(plaintextPassword, passwordHash)) {
            registerFailedAttempt(now);
            throw new InvalidCredentialsException();
        }
        failedLoginAttempts = 0;
        lockedUntil = null;
        touch(now);
    }

    public void assignMembership(TenantMembership membership, Instant now) {
        Objects.requireNonNull(membership, "membership");
        if (type == AccountType.CITIZEN) {
            throw new DomainException(
                    "CITIZEN_TENANT_FORBIDDEN",
                    "Citizen Tenant Forbidden",
                    "Citizen accounts cannot receive tenant employee memberships",
                    422);
        }
        membershipsByTenant.put(membership.tenantId().value(), membership);
        touch(now);
        record(new UserMembershipChanged(id, membership.tenantId(), now));
    }

    public void disable(Instant now) {
        if (status == AccountStatus.DISABLED) {
            return;
        }
        status = AccountStatus.DISABLED;
        touch(now);
        record(new UserAccountDisabled(id, now));
    }

    public void unlock(Instant now) {
        if (status == AccountStatus.DISABLED) {
            throw new DomainException(
                    "ACCOUNT_DISABLED",
                    "Account Disabled",
                    "Disabled accounts cannot be unlocked; re-enable first",
                    409);
        }
        status = AccountStatus.ACTIVE;
        failedLoginAttempts = 0;
        lockedUntil = null;
        touch(now);
    }

    public Optional<TenantMembership> membership(TenantId tenantId) {
        return Optional.ofNullable(membershipsByTenant.get(tenantId.value()));
    }

    public List<TenantMembership> memberships() {
        return List.copyOf(membershipsByTenant.values());
    }

    public List<IdentityDomainEvent> pullDomainEvents() {
        List<IdentityDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void ensureInteractive() {
        if (type == AccountType.SERVICE_ACCOUNT) {
            throw new DomainException(
                    "SERVICE_ACCOUNT_PASSWORD_LOGIN",
                    "Password Login Forbidden",
                    "Service accounts authenticate via OAuth client credentials",
                    403);
        }
    }

    private void ensureCanAuthenticate(Instant now) {
        if (status == AccountStatus.DISABLED) {
            throw new DomainException(
                    "ACCOUNT_DISABLED", "Account Disabled", "Account is disabled", 403);
        }
        if (status == AccountStatus.LOCKED
                && lockedUntil != null
                && lockedUntil.isAfter(now)) {
            throw new DomainException(
                    "ACCOUNT_LOCKED",
                    "Account Locked",
                    "Account is locked until " + lockedUntil,
                    403);
        }
        if (status == AccountStatus.LOCKED && (lockedUntil == null || !lockedUntil.isAfter(now))) {
            status = AccountStatus.ACTIVE;
            failedLoginAttempts = 0;
            lockedUntil = null;
        }
    }

    private void registerFailedAttempt(Instant now) {
        failedLoginAttempts++;
        if (failedLoginAttempts >= PasswordPolicy.MAX_FAILED_ATTEMPTS) {
            status = AccountStatus.LOCKED;
            lockedUntil = now.plusSeconds(15 * 60);
            record(new UserAccountLocked(id, lockedUntil, now));
        }
        touch(now);
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(IdentityDomainEvent event) {
        domainEvents.add(event);
    }

    public UserAccountId id() {
        return id;
    }

    public Username username() {
        return username;
    }

    public AccountType type() {
        return type;
    }

    public AccountStatus status() {
        return status;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public int failedLoginAttempts() {
        return failedLoginAttempts;
    }

    public Instant lockedUntil() {
        return lockedUntil;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long version() {
        return version;
    }

    public Map<String, TenantMembership> membershipMap() {
        return Collections.unmodifiableMap(membershipsByTenant);
    }
}
