package et.gov.ethgov.identity.domain.client;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * OAuth2/OIDC client registration owned by Identity Platform.
 */
public final class OAuthClient {

    private final OAuthClientId id;
    private final String clientId;
    private final String clientSecretHash;
    private final TenantId tenantId;
    private final Set<String> redirectUris;
    private final Set<String> grantTypes;
    private final Set<String> scopes;
    private final boolean confidential;
    private final Instant createdAt;
    private boolean active;

    private OAuthClient(
            OAuthClientId id,
            String clientId,
            String clientSecretHash,
            TenantId tenantId,
            Set<String> redirectUris,
            Set<String> grantTypes,
            Set<String> scopes,
            boolean confidential,
            Instant createdAt,
            boolean active) {
        this.id = id;
        this.clientId = clientId;
        this.clientSecretHash = clientSecretHash;
        this.tenantId = tenantId;
        this.redirectUris = redirectUris;
        this.grantTypes = grantTypes;
        this.scopes = scopes;
        this.confidential = confidential;
        this.createdAt = createdAt;
        this.active = active;
    }

    public static OAuthClient registerConfidential(
            String clientId,
            String clientSecretHash,
            TenantId tenantId,
            Set<String> redirectUris,
            Set<String> grantTypes,
            Set<String> scopes,
            Instant now) {
        validateClientId(clientId);
        Objects.requireNonNull(clientSecretHash, "clientSecretHash");
        Objects.requireNonNull(tenantId, "tenantId");
        Set<String> redirects = requireNonEmpty(redirectUris, "redirectUris");
        Set<String> grants = requireNonEmpty(grantTypes, "grantTypes");
        Set<String> scopeSet = requireNonEmpty(scopes, "scopes");
        if (!grants.contains("authorization_code") && !grants.contains("client_credentials")) {
            throw new IllegalArgumentException(
                    "grantTypes must include authorization_code and/or client_credentials");
        }
        if (grants.contains("authorization_code") && redirects.isEmpty()) {
            throw new IllegalArgumentException("authorization_code requires redirect URIs");
        }
        return new OAuthClient(
                OAuthClientId.generate(),
                clientId.trim(),
                clientSecretHash,
                tenantId,
                redirects,
                grants,
                scopeSet,
                true,
                now,
                true);
    }

    public static OAuthClient rehydrate(
            OAuthClientId id,
            String clientId,
            String clientSecretHash,
            TenantId tenantId,
            Set<String> redirectUris,
            Set<String> grantTypes,
            Set<String> scopes,
            boolean confidential,
            Instant createdAt,
            boolean active) {
        return new OAuthClient(
                id,
                clientId,
                clientSecretHash,
                tenantId,
                Set.copyOf(redirectUris),
                Set.copyOf(grantTypes),
                Set.copyOf(scopes),
                confidential,
                createdAt,
                active);
    }

    public void deactivate() {
        this.active = false;
    }

    private static void validateClientId(String clientId) {
        Objects.requireNonNull(clientId, "clientId");
        String normalized = clientId.trim();
        if (normalized.length() < 3 || normalized.length() > 100) {
            throw new IllegalArgumentException("clientId length invalid");
        }
        if (!normalized.matches("^[a-z0-9._-]+$")) {
            throw new IllegalArgumentException("clientId must be lowercase alphanumeric/._-");
        }
    }

    private static Set<String> requireNonEmpty(Set<String> values, String field) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(field + " required");
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String value : values) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(field + " contains blank");
            }
            normalized.add(value.trim());
        }
        return Collections.unmodifiableSet(normalized);
    }

    public OAuthClientId id() {
        return id;
    }

    public String clientId() {
        return clientId;
    }

    public String clientSecretHash() {
        return clientSecretHash;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public Set<String> redirectUris() {
        return redirectUris;
    }

    public Set<String> grantTypes() {
        return grantTypes;
    }

    public Set<String> scopes() {
        return scopes;
    }

    public boolean confidential() {
        return confidential;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public boolean active() {
        return active;
    }
}
