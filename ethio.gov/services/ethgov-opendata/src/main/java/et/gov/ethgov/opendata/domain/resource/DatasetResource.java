package et.gov.ethgov.opendata.domain.resource;

import et.gov.ethgov.opendata.domain.dataset.DatasetId;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DatasetResource {
    private final ResourceId id;
    private final TenantId tenantId;
    private final ResourceNumber resourceNumber;
    private final DatasetId datasetId;
    private String title;
    private String description;
    private ResourceFormat format;
    private String accessUrl;
    private String documentId;
    private String mediaType;
    private ResourceStatus status;
    private Instant addedAt;
    private Instant updatedAt;
    private long version;
    private final List<ResourceDomainEvent> domainEvents = new ArrayList<>();

    private DatasetResource(
            ResourceId id,
            TenantId tenantId,
            ResourceNumber resourceNumber,
            DatasetId datasetId,
            String title,
            String description,
            ResourceFormat format,
            String accessUrl,
            String documentId,
            String mediaType,
            ResourceStatus status,
            Instant addedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.resourceNumber = resourceNumber;
        this.datasetId = datasetId;
        this.title = title;
        this.description = description;
        this.format = format;
        this.accessUrl = accessUrl;
        this.documentId = documentId;
        this.mediaType = mediaType;
        this.status = status;
        this.addedAt = addedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static DatasetResource add(
            TenantId tenantId,
            ResourceNumber resourceNumber,
            DatasetId datasetId,
            String title,
            String description,
            ResourceFormat format,
            String accessUrl,
            String documentId,
            String mediaType,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(resourceNumber, "resourceNumber");
        Objects.requireNonNull(datasetId, "datasetId");
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(now, "now");
        String url = blankToNull(accessUrl);
        String doc = validateDoc(documentId);
        if (url == null && doc == null) {
            throw new IllegalArgumentException("accessUrl or documentId is required");
        }
        if (url != null && !url.startsWith("https://") && !url.startsWith("http://")) {
            throw new IllegalArgumentException("accessUrl must be an http(s) URL");
        }
        DatasetResource resource = new DatasetResource(
                ResourceId.generate(),
                tenantId,
                resourceNumber,
                datasetId,
                requireTitle(title),
                blankToNull(description),
                format,
                url,
                doc,
                blankToNull(mediaType),
                ResourceStatus.ACTIVE,
                now,
                now,
                0L);
        resource.record(new ResourceAdded(resource.id, tenantId, resourceNumber, datasetId, format, now));
        return resource;
    }

    public static DatasetResource rehydrate(
            ResourceId id,
            TenantId tenantId,
            ResourceNumber resourceNumber,
            DatasetId datasetId,
            String title,
            String description,
            ResourceFormat format,
            String accessUrl,
            String documentId,
            String mediaType,
            ResourceStatus status,
            Instant addedAt,
            Instant updatedAt,
            long version) {
        return new DatasetResource(
                id,
                tenantId,
                resourceNumber,
                datasetId,
                title,
                description,
                format,
                accessUrl,
                documentId,
                mediaType,
                status,
                addedAt,
                updatedAt,
                version);
    }

    public void update(
            String title,
            String description,
            ResourceFormat format,
            String accessUrl,
            String documentId,
            String mediaType,
            Instant now) {
        if (status != ResourceStatus.ACTIVE) {
            throw new DomainException(
                    "RESOURCE_NOT_ACTIVE",
                    "Resource Not Active",
                    "Only ACTIVE resources can be updated",
                    409);
        }
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(now, "now");
        String url = blankToNull(accessUrl);
        String doc = validateDoc(documentId);
        if (url == null && doc == null) {
            throw new IllegalArgumentException("accessUrl or documentId is required");
        }
        if (url != null && !url.startsWith("https://") && !url.startsWith("http://")) {
            throw new IllegalArgumentException("accessUrl must be an http(s) URL");
        }
        this.title = requireTitle(title);
        this.description = blankToNull(description);
        this.format = format;
        this.accessUrl = url;
        this.documentId = doc;
        this.mediaType = blankToNull(mediaType);
        this.updatedAt = now;
        record(new ResourceUpdated(id, now));
    }

    public void retire(String reason, Instant now) {
        if (status == ResourceStatus.RETIRED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        this.status = ResourceStatus.RETIRED;
        this.updatedAt = now;
        record(new ResourceRetired(id, reason.trim(), now));
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

    private static String validateDoc(String documentId) {
        if (documentId == null || documentId.isBlank()) {
            return null;
        }
        String n = documentId.trim();
        if (!n.startsWith("doc_") || n.length() < 40) {
            throw new IllegalArgumentException("documentId must be a valid doc_ identifier");
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

    private void record(ResourceDomainEvent event) {
        domainEvents.add(event);
    }

    public List<ResourceDomainEvent> pullDomainEvents() {
        List<ResourceDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public ResourceId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public ResourceNumber resourceNumber() {
        return resourceNumber;
    }

    public DatasetId datasetId() {
        return datasetId;
    }

    public String title() {
        return title;
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public ResourceFormat format() {
        return format;
    }

    public Optional<String> accessUrl() {
        return Optional.ofNullable(accessUrl);
    }

    public Optional<String> documentId() {
        return Optional.ofNullable(documentId);
    }

    public Optional<String> mediaType() {
        return Optional.ofNullable(mediaType);
    }

    public ResourceStatus status() {
        return status;
    }

    public Instant addedAt() {
        return addedAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return version;
    }
}
