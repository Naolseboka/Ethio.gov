package et.gov.ethgov.document.domain.document;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Governed document metadata. Binary bytes live in object storage referenced by {@link StorageObjectKey}.
 */
public final class Document {

    public static final long MAX_SIZE_BYTES = 50L * 1024 * 1024;

    private final DocumentId id;
    private final TenantId tenantId;
    private String title;
    private String description;
    private DocumentClassification classification;
    private DocumentStatus status;
    private final ContentType contentType;
    private final FileName originalFileName;
    private final StorageObjectKey storageObjectKey;
    private final int version;
    private Long sizeBytes;
    private ContentHash contentHash;
    private DocumentOwnerRef owner;
    private final String uploadedByAccountId;
    private final Instant createdAt;
    private Instant updatedAt;
    private long entityVersion;
    private final List<DocumentDomainEvent> domainEvents = new ArrayList<>();

    private Document(
            DocumentId id,
            TenantId tenantId,
            String title,
            String description,
            DocumentClassification classification,
            DocumentStatus status,
            ContentType contentType,
            FileName originalFileName,
            StorageObjectKey storageObjectKey,
            int version,
            Long sizeBytes,
            ContentHash contentHash,
            DocumentOwnerRef owner,
            String uploadedByAccountId,
            Instant createdAt,
            Instant updatedAt,
            long entityVersion) {
        this.id = id;
        this.tenantId = tenantId;
        this.title = title;
        this.description = description;
        this.classification = classification;
        this.status = status;
        this.contentType = contentType;
        this.originalFileName = originalFileName;
        this.storageObjectKey = storageObjectKey;
        this.version = version;
        this.sizeBytes = sizeBytes;
        this.contentHash = contentHash;
        this.owner = owner;
        this.uploadedByAccountId = uploadedByAccountId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.entityVersion = entityVersion;
    }

    public static Document createDraft(
            TenantId tenantId,
            String title,
            String description,
            DocumentClassification classification,
            ContentType contentType,
            FileName originalFileName,
            DocumentOwnerRef owner,
            String uploadedByAccountId,
            long expectedSizeBytes,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(classification, "classification");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(originalFileName, "originalFileName");
        validateExpectedSize(expectedSizeBytes);
        DocumentId id = DocumentId.generate();
        Document document = new Document(
                id,
                tenantId,
                requireTitle(title),
                normalizeOptional(description, "description", 1000),
                classification,
                DocumentStatus.DRAFT,
                contentType,
                originalFileName,
                StorageObjectKey.forDocument(tenantId, id, 1),
                1,
                null,
                null,
                owner,
                normalizeUploader(uploadedByAccountId),
                now,
                now,
                0);
        document.record(new DocumentDraftCreated(
                id, tenantId, contentType, document.storageObjectKey, now));
        return document;
    }

    public static Document rehydrate(
            DocumentId id,
            TenantId tenantId,
            String title,
            String description,
            DocumentClassification classification,
            DocumentStatus status,
            ContentType contentType,
            FileName originalFileName,
            StorageObjectKey storageObjectKey,
            int version,
            Long sizeBytes,
            ContentHash contentHash,
            DocumentOwnerRef owner,
            String uploadedByAccountId,
            Instant createdAt,
            Instant updatedAt,
            long entityVersion) {
        return new Document(
                id,
                tenantId,
                title,
                description,
                classification,
                status,
                contentType,
                originalFileName,
                storageObjectKey,
                version,
                sizeBytes,
                contentHash,
                owner,
                uploadedByAccountId,
                createdAt,
                updatedAt,
                entityVersion);
    }

    public void completeUpload(long sizeBytes, ContentHash contentHash, Instant now) {
        if (status != DocumentStatus.DRAFT) {
            throw new DomainException(
                    "DOCUMENT_NOT_DRAFT",
                    "Document Not Draft",
                    "Only draft documents can complete upload",
                    409);
        }
        validateExpectedSize(sizeBytes);
        Objects.requireNonNull(contentHash, "contentHash");
        this.sizeBytes = sizeBytes;
        this.contentHash = contentHash;
        this.status = DocumentStatus.ACTIVE;
        touch(now);
        record(new DocumentUploaded(id, contentHash, sizeBytes, now));
    }

    public void updateMetadata(
            String title, String description, DocumentClassification classification, Instant now) {
        ensureMutable();
        Objects.requireNonNull(classification, "classification");
        this.title = requireTitle(title);
        this.description = normalizeOptional(description, "description", 1000);
        this.classification = classification;
        touch(now);
        record(new DocumentMetadataUpdated(id, now));
    }

    public void assignOwner(DocumentOwnerRef owner, Instant now) {
        ensureMutable();
        Objects.requireNonNull(owner, "owner");
        this.owner = owner;
        touch(now);
        record(new DocumentMetadataUpdated(id, now));
    }

    public void archive(Instant now) {
        if (status == DocumentStatus.DELETED) {
            throw new DomainException(
                    "DOCUMENT_DELETED",
                    "Document Deleted",
                    "Deleted documents cannot be archived",
                    409);
        }
        if (status == DocumentStatus.ARCHIVED) {
            return;
        }
        if (status == DocumentStatus.DRAFT) {
            throw new DomainException(
                    "DOCUMENT_NOT_ACTIVE",
                    "Document Not Active",
                    "Draft documents cannot be archived; delete the draft instead",
                    409);
        }
        this.status = DocumentStatus.ARCHIVED;
        touch(now);
        record(new DocumentArchived(id, now));
    }

    public void softDelete(Instant now) {
        if (status == DocumentStatus.DELETED) {
            return;
        }
        this.status = DocumentStatus.DELETED;
        touch(now);
        record(new DocumentDeleted(id, now));
    }

    public List<DocumentDomainEvent> pullDomainEvents() {
        List<DocumentDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void ensureMutable() {
        if (status == DocumentStatus.DELETED) {
            throw new DomainException(
                    "DOCUMENT_DELETED",
                    "Document Deleted",
                    "Deleted documents cannot be modified",
                    409);
        }
        if (status == DocumentStatus.ARCHIVED) {
            throw new DomainException(
                    "DOCUMENT_ARCHIVED",
                    "Document Archived",
                    "Archived documents cannot be modified",
                    409);
        }
    }

    private static void validateExpectedSize(long sizeBytes) {
        if (sizeBytes <= 0 || sizeBytes > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "sizeBytes must be between 1 and " + MAX_SIZE_BYTES);
        }
    }

    private static String requireTitle(String title) {
        Objects.requireNonNull(title, "title");
        String normalized = title.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2 || normalized.length() > 200) {
            throw new IllegalArgumentException("title length must be between 2 and 200");
        }
        return normalized;
    }

    private static String normalizeOptional(String value, String field, int max) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() > max) {
            throw new IllegalArgumentException(field + " must be <= " + max + " characters");
        }
        return normalized;
    }

    private static String normalizeUploader(String uploadedByAccountId) {
        if (uploadedByAccountId == null || uploadedByAccountId.isBlank()) {
            return null;
        }
        String normalized = uploadedByAccountId.trim();
        if (!normalized.startsWith("usr_") || normalized.length() < 40) {
            throw new IllegalArgumentException("uploadedByAccountId must be a UserAccountId");
        }
        return normalized;
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(DocumentDomainEvent event) {
        domainEvents.add(event);
    }

    public DocumentId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String title() {
        return title;
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public DocumentClassification classification() {
        return classification;
    }

    public DocumentStatus status() {
        return status;
    }

    public ContentType contentType() {
        return contentType;
    }

    public FileName originalFileName() {
        return originalFileName;
    }

    public StorageObjectKey storageObjectKey() {
        return storageObjectKey;
    }

    public int version() {
        return version;
    }

    public Optional<Long> sizeBytes() {
        return Optional.ofNullable(sizeBytes);
    }

    public Optional<ContentHash> contentHash() {
        return Optional.ofNullable(contentHash);
    }

    public Optional<DocumentOwnerRef> owner() {
        return Optional.ofNullable(owner);
    }

    public Optional<String> uploadedByAccountId() {
        return Optional.ofNullable(uploadedByAccountId);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return entityVersion;
    }
}
