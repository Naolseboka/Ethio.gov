package et.gov.ethgov.search.domain.document;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SearchDocument {
    private final SearchDocumentId id;
    private final TenantId tenantId;
    private final DocumentNumber documentNumber;
    private final SourceType sourceType;
    private final SourceReference sourceId;
    private String title;
    private String summary;
    private String bodyText;
    private SearchLocale locale;
    private String tagsJson;
    private SearchDocumentStatus status;
    private Instant indexedAt;
    private Instant updatedAt;
    private long version;
    private final List<SearchDocumentDomainEvent> domainEvents = new ArrayList<>();

    private SearchDocument(
            SearchDocumentId id,
            TenantId tenantId,
            DocumentNumber documentNumber,
            SourceType sourceType,
            SourceReference sourceId,
            String title,
            String summary,
            String bodyText,
            SearchLocale locale,
            String tagsJson,
            SearchDocumentStatus status,
            Instant indexedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.documentNumber = documentNumber;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.title = title;
        this.summary = summary;
        this.bodyText = bodyText;
        this.locale = locale;
        this.tagsJson = tagsJson;
        this.status = status;
        this.indexedAt = indexedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static SearchDocument index(
            TenantId tenantId,
            DocumentNumber documentNumber,
            SourceType sourceType,
            SourceReference sourceId,
            String title,
            String summary,
            String bodyText,
            SearchLocale locale,
            String tagsJson,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(documentNumber, "documentNumber");
        Objects.requireNonNull(sourceType, "sourceType");
        Objects.requireNonNull(sourceId, "sourceId");
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(now, "now");
        String normalizedTitle = requireTitle(title);
        String normalizedBody = requireBody(bodyText);
        SearchDocument document = new SearchDocument(
                SearchDocumentId.generate(),
                tenantId,
                documentNumber,
                sourceType,
                sourceId,
                normalizedTitle,
                blankToNull(summary),
                normalizedBody,
                locale,
                normalizeTags(tagsJson),
                SearchDocumentStatus.ACTIVE,
                now,
                now,
                0L);
        document.record(new SearchDocumentIndexed(
                document.id, tenantId, documentNumber, sourceType, sourceId, now));
        return document;
    }

    public static SearchDocument rehydrate(
            SearchDocumentId id,
            TenantId tenantId,
            DocumentNumber documentNumber,
            SourceType sourceType,
            SourceReference sourceId,
            String title,
            String summary,
            String bodyText,
            SearchLocale locale,
            String tagsJson,
            SearchDocumentStatus status,
            Instant indexedAt,
            Instant updatedAt,
            long version) {
        return new SearchDocument(
                id,
                tenantId,
                documentNumber,
                sourceType,
                sourceId,
                title,
                summary,
                bodyText,
                locale,
                tagsJson,
                status,
                indexedAt,
                updatedAt,
                version);
    }

    public void update(
            String title, String summary, String bodyText, SearchLocale locale, String tagsJson, Instant now) {
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(now, "now");
        if (status != SearchDocumentStatus.ACTIVE) {
            throw new DomainException(
                    "DOCUMENT_NOT_ACTIVE",
                    "Document Not Active",
                    "Only ACTIVE search documents can be updated",
                    409);
        }
        this.title = requireTitle(title);
        this.summary = blankToNull(summary);
        this.bodyText = requireBody(bodyText);
        this.locale = locale;
        this.tagsJson = normalizeTags(tagsJson);
        this.updatedAt = now;
        record(new SearchDocumentUpdated(id, now));
    }

    /** Re-activate a retired document with fresh content (re-index). */
    public void reactivate(
            String title, String summary, String bodyText, SearchLocale locale, String tagsJson, Instant now) {
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(now, "now");
        if (status != SearchDocumentStatus.RETIRED) {
            throw new DomainException(
                    "DOCUMENT_NOT_RETIRED",
                    "Document Not Retired",
                    "Only RETIRED search documents can be reactivated",
                    409);
        }
        this.status = SearchDocumentStatus.ACTIVE;
        this.title = requireTitle(title);
        this.summary = blankToNull(summary);
        this.bodyText = requireBody(bodyText);
        this.locale = locale;
        this.tagsJson = normalizeTags(tagsJson);
        this.indexedAt = now;
        this.updatedAt = now;
        record(new SearchDocumentIndexed(id, tenantId, documentNumber, sourceType, sourceId, now));
    }

    public void retire(String reason, Instant now) {
        if (status == SearchDocumentStatus.RETIRED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != SearchDocumentStatus.ACTIVE) {
            throw new DomainException(
                    "INVALID_DOCUMENT_STATE",
                    "Invalid Document State",
                    "Cannot retire search document in status " + status,
                    409);
        }
        this.status = SearchDocumentStatus.RETIRED;
        this.updatedAt = now;
        record(new SearchDocumentRetired(id, reason.trim(), now));
    }

    private static String requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        String n = title.trim();
        if (n.length() > 300) {
            throw new IllegalArgumentException("title length must be <= 300");
        }
        return n;
    }

    private static String requireBody(String bodyText) {
        if (bodyText == null || bodyText.isBlank()) {
            throw new IllegalArgumentException("bodyText is required");
        }
        String n = bodyText.trim();
        if (n.length() > 50_000) {
            throw new IllegalArgumentException("bodyText length must be <= 50000");
        }
        return n;
    }

    private static String normalizeTags(String tagsJson) {
        if (tagsJson == null || tagsJson.isBlank()) {
            return "[]";
        }
        String n = tagsJson.trim();
        if (!n.startsWith("[") || !n.endsWith("]")) {
            throw new IllegalArgumentException("tagsJson must be a JSON array");
        }
        if (n.length() > 4000) {
            throw new IllegalArgumentException("tagsJson length must be <= 4000");
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

    private void record(SearchDocumentDomainEvent event) {
        domainEvents.add(event);
    }

    public List<SearchDocumentDomainEvent> pullDomainEvents() {
        List<SearchDocumentDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public SearchDocumentId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public DocumentNumber documentNumber() {
        return documentNumber;
    }

    public SourceType sourceType() {
        return sourceType;
    }

    public SourceReference sourceId() {
        return sourceId;
    }

    public String title() {
        return title;
    }

    public Optional<String> summary() {
        return Optional.ofNullable(summary);
    }

    public String bodyText() {
        return bodyText;
    }

    public SearchLocale locale() {
        return locale;
    }

    public String tagsJson() {
        return tagsJson;
    }

    public SearchDocumentStatus status() {
        return status;
    }

    public Instant indexedAt() {
        return indexedAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return version;
    }
}
