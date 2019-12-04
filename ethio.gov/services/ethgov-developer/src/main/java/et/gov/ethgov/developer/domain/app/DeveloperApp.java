package et.gov.ethgov.developer.domain.app;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class DeveloperApp {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final DeveloperAppId id;
    private final TenantId tenantId;
    private final AppNumber appNumber;
    private final AppSlug slug;
    private String name;
    private String description;
    private String ownerOrgId;
    private String contactEmail;
    private AllowedScopes allowedScopes;
    private DeveloperAppStatus status;
    private Instant registeredAt;
    private Instant activatedAt;
    private Instant updatedAt;
    private long version;
    private final List<DeveloperAppDomainEvent> domainEvents = new ArrayList<>();

    private DeveloperApp(
            DeveloperAppId id,
            TenantId tenantId,
            AppNumber appNumber,
            AppSlug slug,
            String name,
            String description,
            String ownerOrgId,
            String contactEmail,
            AllowedScopes allowedScopes,
            DeveloperAppStatus status,
            Instant registeredAt,
            Instant activatedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.appNumber = appNumber;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.ownerOrgId = ownerOrgId;
        this.contactEmail = contactEmail;
        this.allowedScopes = allowedScopes;
        this.status = status;
        this.registeredAt = registeredAt;
        this.activatedAt = activatedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static DeveloperApp register(
            TenantId tenantId,
            AppNumber appNumber,
            AppSlug slug,
            String name,
            String description,
            String ownerOrgId,
            String contactEmail,
            AllowedScopes allowedScopes,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(appNumber, "appNumber");
        Objects.requireNonNull(slug, "slug");
        Objects.requireNonNull(allowedScopes, "allowedScopes");
        Objects.requireNonNull(now, "now");
        if (allowedScopes.asSet().isEmpty()) {
            throw new IllegalArgumentException("at least one scope is required");
        }
        DeveloperApp app = new DeveloperApp(
                DeveloperAppId.generate(),
                tenantId,
                appNumber,
                slug,
                requireName(name),
                blankToNull(description),
                validateOrg(ownerOrgId),
                requireEmail(contactEmail),
                allowedScopes,
                DeveloperAppStatus.DRAFT,
                now,
                null,
                now,
                0L);
        app.record(new DeveloperAppRegistered(app.id, tenantId, appNumber, slug, now));
        return app;
    }

    public static DeveloperApp rehydrate(
            DeveloperAppId id,
            TenantId tenantId,
            AppNumber appNumber,
            AppSlug slug,
            String name,
            String description,
            String ownerOrgId,
            String contactEmail,
            AllowedScopes allowedScopes,
            DeveloperAppStatus status,
            Instant registeredAt,
            Instant activatedAt,
            Instant updatedAt,
            long version) {
        return new DeveloperApp(
                id,
                tenantId,
                appNumber,
                slug,
                name,
                description,
                ownerOrgId,
                contactEmail,
                allowedScopes,
                status,
                registeredAt,
                activatedAt,
                updatedAt,
                version);
    }

    public void update(
            String name,
            String description,
            String ownerOrgId,
            String contactEmail,
            AllowedScopes allowedScopes,
            Instant now) {
        if (status != DeveloperAppStatus.DRAFT && status != DeveloperAppStatus.ACTIVE) {
            throw new DomainException(
                    "APP_NOT_EDITABLE",
                    "App Not Editable",
                    "Only DRAFT or ACTIVE apps can be updated",
                    409);
        }
        Objects.requireNonNull(allowedScopes, "allowedScopes");
        Objects.requireNonNull(now, "now");
        if (allowedScopes.asSet().isEmpty()) {
            throw new IllegalArgumentException("at least one scope is required");
        }
        this.name = requireName(name);
        this.description = blankToNull(description);
        this.ownerOrgId = validateOrg(ownerOrgId);
        this.contactEmail = requireEmail(contactEmail);
        this.allowedScopes = allowedScopes;
        this.updatedAt = now;
        record(new DeveloperAppUpdated(id, now));
    }

    public void activate(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != DeveloperAppStatus.DRAFT && status != DeveloperAppStatus.SUSPENDED) {
            throw invalidState("activate");
        }
        this.status = DeveloperAppStatus.ACTIVE;
        if (this.activatedAt == null) {
            this.activatedAt = now;
        }
        this.updatedAt = now;
        record(new DeveloperAppActivated(id, now));
    }

    public void suspend(String reason, Instant now) {
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != DeveloperAppStatus.ACTIVE) {
            throw invalidState("suspend");
        }
        this.status = DeveloperAppStatus.SUSPENDED;
        this.updatedAt = now;
        record(new DeveloperAppSuspended(id, reason.trim(), now));
    }

    public void retire(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != DeveloperAppStatus.ACTIVE && status != DeveloperAppStatus.SUSPENDED) {
            throw invalidState("retire");
        }
        this.status = DeveloperAppStatus.RETIRED;
        this.updatedAt = now;
        record(new DeveloperAppRetired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == DeveloperAppStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != DeveloperAppStatus.DRAFT) {
            throw invalidState("cancel");
        }
        this.status = DeveloperAppStatus.CANCELLED;
        this.updatedAt = now;
        record(new DeveloperAppCancelled(id, reason.trim(), now));
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_APP_STATE",
                "Invalid App State",
                "Cannot " + action + " developer app in status " + status,
                409);
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        String n = name.trim();
        if (n.length() > 200) {
            throw new IllegalArgumentException("name length must be <= 200");
        }
        return n;
    }

    private static String requireEmail(String contactEmail) {
        if (contactEmail == null || contactEmail.isBlank()) {
            throw new IllegalArgumentException("contactEmail is required");
        }
        String n = contactEmail.trim().toLowerCase(Locale.ROOT);
        if (n.length() > 254 || !EMAIL.matcher(n).matches()) {
            throw new IllegalArgumentException("contactEmail is invalid");
        }
        return n;
    }

    private static String validateOrg(String ownerOrgId) {
        if (ownerOrgId == null || ownerOrgId.isBlank()) {
            return null;
        }
        String n = ownerOrgId.trim();
        if (!n.startsWith("org_") || n.length() < 40) {
            throw new IllegalArgumentException("ownerOrgId must be a valid org_ identifier");
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

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void record(DeveloperAppDomainEvent event) {
        domainEvents.add(event);
    }

    public List<DeveloperAppDomainEvent> pullDomainEvents() {
        List<DeveloperAppDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public DeveloperAppId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public AppNumber appNumber() {
        return appNumber;
    }

    public AppSlug slug() {
        return slug;
    }

    public String name() {
        return name;
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public Optional<String> ownerOrgId() {
        return Optional.ofNullable(ownerOrgId);
    }

    public String contactEmail() {
        return contactEmail;
    }

    public AllowedScopes allowedScopes() {
        return allowedScopes;
    }

    public DeveloperAppStatus status() {
        return status;
    }

    public Instant registeredAt() {
        return registeredAt;
    }

    public Optional<Instant> activatedAt() {
        return Optional.ofNullable(activatedAt);
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return version;
    }
}
