package et.gov.ethgov.opendata.domain.dataset;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class OpenDataset {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final DatasetId id;
    private final TenantId tenantId;
    private final DatasetNumber datasetNumber;
    private final DatasetSlug slug;
    private String title;
    private String description;
    private DatasetTheme theme;
    private OpenLicense license;
    private String publisherOrgId;
    private String contactEmail;
    private String locale;
    private DatasetStatus status;
    private Instant registeredAt;
    private Instant publishedAt;
    private Instant updatedAt;
    private long version;
    private final List<DatasetDomainEvent> domainEvents = new ArrayList<>();

    private OpenDataset(
            DatasetId id,
            TenantId tenantId,
            DatasetNumber datasetNumber,
            DatasetSlug slug,
            String title,
            String description,
            DatasetTheme theme,
            OpenLicense license,
            String publisherOrgId,
            String contactEmail,
            String locale,
            DatasetStatus status,
            Instant registeredAt,
            Instant publishedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.datasetNumber = datasetNumber;
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.theme = theme;
        this.license = license;
        this.publisherOrgId = publisherOrgId;
        this.contactEmail = contactEmail;
        this.locale = locale;
        this.status = status;
        this.registeredAt = registeredAt;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static OpenDataset register(
            TenantId tenantId,
            DatasetNumber datasetNumber,
            DatasetSlug slug,
            String title,
            String description,
            DatasetTheme theme,
            OpenLicense license,
            String publisherOrgId,
            String contactEmail,
            String locale,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(datasetNumber, "datasetNumber");
        Objects.requireNonNull(slug, "slug");
        Objects.requireNonNull(theme, "theme");
        Objects.requireNonNull(license, "license");
        Objects.requireNonNull(now, "now");
        OpenDataset dataset = new OpenDataset(
                DatasetId.generate(),
                tenantId,
                datasetNumber,
                slug,
                requireTitle(title),
                requireDescription(description),
                theme,
                license,
                validateOrg(publisherOrgId),
                validateEmail(contactEmail),
                requireLocale(locale),
                DatasetStatus.DRAFT,
                now,
                null,
                now,
                0L);
        dataset.record(new DatasetRegistered(dataset.id, tenantId, datasetNumber, slug, theme, now));
        return dataset;
    }

    public static OpenDataset rehydrate(
            DatasetId id,
            TenantId tenantId,
            DatasetNumber datasetNumber,
            DatasetSlug slug,
            String title,
            String description,
            DatasetTheme theme,
            OpenLicense license,
            String publisherOrgId,
            String contactEmail,
            String locale,
            DatasetStatus status,
            Instant registeredAt,
            Instant publishedAt,
            Instant updatedAt,
            long version) {
        return new OpenDataset(
                id,
                tenantId,
                datasetNumber,
                slug,
                title,
                description,
                theme,
                license,
                publisherOrgId,
                contactEmail,
                locale,
                status,
                registeredAt,
                publishedAt,
                updatedAt,
                version);
    }

    public void update(
            String title,
            String description,
            DatasetTheme theme,
            OpenLicense license,
            String publisherOrgId,
            String contactEmail,
            String locale,
            Instant now) {
        if (status != DatasetStatus.DRAFT) {
            throw new DomainException(
                    "DATASET_NOT_EDITABLE",
                    "Dataset Not Editable",
                    "Only DRAFT datasets can be updated",
                    409);
        }
        Objects.requireNonNull(theme, "theme");
        Objects.requireNonNull(license, "license");
        Objects.requireNonNull(now, "now");
        this.title = requireTitle(title);
        this.description = requireDescription(description);
        this.theme = theme;
        this.license = license;
        this.publisherOrgId = validateOrg(publisherOrgId);
        this.contactEmail = validateEmail(contactEmail);
        this.locale = requireLocale(locale);
        this.updatedAt = now;
        record(new DatasetUpdated(id, now));
    }

    public void publish(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != DatasetStatus.DRAFT) {
            throw invalidState("publish");
        }
        this.status = DatasetStatus.PUBLISHED;
        this.publishedAt = now;
        this.updatedAt = now;
        record(new DatasetPublished(id, now));
    }

    public void retire(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != DatasetStatus.PUBLISHED) {
            throw invalidState("retire");
        }
        this.status = DatasetStatus.RETIRED;
        this.updatedAt = now;
        record(new DatasetRetired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == DatasetStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != DatasetStatus.DRAFT) {
            throw invalidState("cancel");
        }
        this.status = DatasetStatus.CANCELLED;
        this.updatedAt = now;
        record(new DatasetCancelled(id, reason.trim(), now));
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_DATASET_STATE",
                "Invalid Dataset State",
                "Cannot " + action + " dataset in status " + status,
                409);
    }

    private static String requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        String n = title.trim();
        if (n.length() > 200) {
            throw new IllegalArgumentException("title length must be <= 200");
        }
        return n;
    }

    private static String requireDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
        String n = description.trim();
        if (n.length() < 10 || n.length() > 4000) {
            throw new IllegalArgumentException("description length must be between 10 and 4000");
        }
        return n;
    }

    private static String requireLocale(String locale) {
        if (locale == null || locale.isBlank()) {
            return "en";
        }
        String n = locale.trim().toLowerCase(Locale.ROOT);
        if (!List.of("am", "en", "om", "ti", "so", "sid").contains(n)) {
            throw new IllegalArgumentException("locale must be one of am/en/om/ti/so/sid");
        }
        return n;
    }

    private static String validateOrg(String publisherOrgId) {
        if (publisherOrgId == null || publisherOrgId.isBlank()) {
            return null;
        }
        String n = publisherOrgId.trim();
        if (!n.startsWith("org_") || n.length() < 40) {
            throw new IllegalArgumentException("publisherOrgId must be a valid org_ identifier");
        }
        return n;
    }

    private static String validateEmail(String contactEmail) {
        if (contactEmail == null || contactEmail.isBlank()) {
            return null;
        }
        String n = contactEmail.trim().toLowerCase(Locale.ROOT);
        if (n.length() > 254 || !EMAIL.matcher(n).matches()) {
            throw new IllegalArgumentException("contactEmail is invalid");
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

    private void record(DatasetDomainEvent event) {
        domainEvents.add(event);
    }

    public List<DatasetDomainEvent> pullDomainEvents() {
        List<DatasetDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public DatasetId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public DatasetNumber datasetNumber() {
        return datasetNumber;
    }

    public DatasetSlug slug() {
        return slug;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public DatasetTheme theme() {
        return theme;
    }

    public OpenLicense license() {
        return license;
    }

    public Optional<String> publisherOrgId() {
        return Optional.ofNullable(publisherOrgId);
    }

    public Optional<String> contactEmail() {
        return Optional.ofNullable(contactEmail);
    }

    public String locale() {
        return locale;
    }

    public DatasetStatus status() {
        return status;
    }

    public Instant registeredAt() {
        return registeredAt;
    }

    public Optional<Instant> publishedAt() {
        return Optional.ofNullable(publishedAt);
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return version;
    }
}
