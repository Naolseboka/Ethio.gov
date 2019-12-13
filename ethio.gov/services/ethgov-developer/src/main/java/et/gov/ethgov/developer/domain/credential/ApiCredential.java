package et.gov.ethgov.developer.domain.credential;

import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ApiCredential {
    private final ApiCredentialId id;
    private final TenantId tenantId;
    private final CredentialNumber credentialNumber;
    private final DeveloperAppId developerAppId;
    private final String keyPrefix;
    private final String keyHash;
    private final String label;
    private final Instant expiresAt;
    private CredentialStatus status;
    private Instant issuedAt;
    private Instant revokedAt;
    private Instant updatedAt;
    private long version;
    private final List<ApiCredentialDomainEvent> domainEvents = new ArrayList<>();

    private ApiCredential(
            ApiCredentialId id,
            TenantId tenantId,
            CredentialNumber credentialNumber,
            DeveloperAppId developerAppId,
            String keyPrefix,
            String keyHash,
            String label,
            Instant expiresAt,
            CredentialStatus status,
            Instant issuedAt,
            Instant revokedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.credentialNumber = credentialNumber;
        this.developerAppId = developerAppId;
        this.keyPrefix = keyPrefix;
        this.keyHash = keyHash;
        this.label = label;
        this.expiresAt = expiresAt;
        this.status = status;
        this.issuedAt = issuedAt;
        this.revokedAt = revokedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static ApiCredential issue(
            TenantId tenantId,
            CredentialNumber credentialNumber,
            DeveloperAppId developerAppId,
            String keyPrefix,
            String keyHash,
            String label,
            Instant expiresAt,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(credentialNumber, "credentialNumber");
        Objects.requireNonNull(developerAppId, "developerAppId");
        Objects.requireNonNull(now, "now");
        requirePrefix(keyPrefix);
        requireHash(keyHash);
        if (expiresAt != null && !expiresAt.isAfter(now)) {
            throw new IllegalArgumentException("expiresAt must be in the future");
        }
        ApiCredential credential = new ApiCredential(
                ApiCredentialId.generate(),
                tenantId,
                credentialNumber,
                developerAppId,
                keyPrefix,
                keyHash,
                requireLabel(label),
                expiresAt,
                CredentialStatus.ACTIVE,
                now,
                null,
                now,
                0L);
        credential.record(new ApiCredentialIssued(
                credential.id, tenantId, credentialNumber, developerAppId, keyPrefix, now));
        return credential;
    }

    public static ApiCredential rehydrate(
            ApiCredentialId id,
            TenantId tenantId,
            CredentialNumber credentialNumber,
            DeveloperAppId developerAppId,
            String keyPrefix,
            String keyHash,
            String label,
            Instant expiresAt,
            CredentialStatus status,
            Instant issuedAt,
            Instant revokedAt,
            Instant updatedAt,
            long version) {
        return new ApiCredential(
                id,
                tenantId,
                credentialNumber,
                developerAppId,
                keyPrefix,
                keyHash,
                label,
                expiresAt,
                status,
                issuedAt,
                revokedAt,
                updatedAt,
                version);
    }

    public void revoke(String reason, Instant now) {
        if (status == CredentialStatus.REVOKED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        this.status = CredentialStatus.REVOKED;
        this.revokedAt = now;
        this.updatedAt = now;
        record(new ApiCredentialRevoked(id, reason.trim(), now));
    }

    public boolean isUsableAt(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != CredentialStatus.ACTIVE) {
            return false;
        }
        return expiresAt == null || expiresAt.isAfter(now);
    }

    private static void requirePrefix(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isBlank()) {
            throw new IllegalArgumentException("keyPrefix is required");
        }
        if (keyPrefix.trim().length() < 8 || keyPrefix.trim().length() > 32) {
            throw new IllegalArgumentException("keyPrefix length must be between 8 and 32");
        }
    }

    private static void requireHash(String keyHash) {
        if (keyHash == null || keyHash.isBlank()) {
            throw new IllegalArgumentException("keyHash is required");
        }
        if (keyHash.trim().length() != 64) {
            throw new IllegalArgumentException("keyHash must be a 64-char hex SHA-256 digest");
        }
    }

    private static String requireLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label is required");
        }
        String n = label.trim();
        if (n.length() > 120) {
            throw new IllegalArgumentException("label length must be <= 120");
        }
        return n;
    }

    private static void requireReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("reason length must be between 5 and 500");
        }
    }

    private void record(ApiCredentialDomainEvent event) {
        domainEvents.add(event);
    }

    public List<ApiCredentialDomainEvent> pullDomainEvents() {
        List<ApiCredentialDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public ApiCredentialId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public CredentialNumber credentialNumber() {
        return credentialNumber;
    }

    public DeveloperAppId developerAppId() {
        return developerAppId;
    }

    public String keyPrefix() {
        return keyPrefix;
    }

    public String keyHash() {
        return keyHash;
    }

    public String label() {
        return label;
    }

    public Optional<Instant> expiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public CredentialStatus status() {
        return status;
    }

    public Instant issuedAt() {
        return issuedAt;
    }

    public Optional<Instant> revokedAt() {
        return Optional.ofNullable(revokedAt);
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return version;
    }
}
