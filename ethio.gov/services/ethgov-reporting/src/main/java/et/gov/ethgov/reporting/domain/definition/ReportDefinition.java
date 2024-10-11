package et.gov.ethgov.reporting.domain.definition;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ReportDefinition {
    private final ReportDefinitionId id;
    private final TenantId tenantId;
    private final ReportNumber reportNumber;
    private final ReportCode code;
    private String name;
    private String description;
    private ReportCategory category;
    private ReportFormat format;
    private ParameterSchema parameterSchema;
    private ReportDefinitionStatus status;
    private Instant registeredAt;
    private Instant publishedAt;
    private Instant updatedAt;
    private long version;
    private final List<ReportDefinitionDomainEvent> domainEvents = new ArrayList<>();

    private ReportDefinition(
            ReportDefinitionId id,
            TenantId tenantId,
            ReportNumber reportNumber,
            ReportCode code,
            String name,
            String description,
            ReportCategory category,
            ReportFormat format,
            ParameterSchema parameterSchema,
            ReportDefinitionStatus status,
            Instant registeredAt,
            Instant publishedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.reportNumber = reportNumber;
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.format = format;
        this.parameterSchema = parameterSchema;
        this.status = status;
        this.registeredAt = registeredAt;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static ReportDefinition register(
            TenantId tenantId,
            ReportNumber reportNumber,
            ReportCode code,
            String name,
            String description,
            ReportCategory category,
            ReportFormat format,
            ParameterSchema parameterSchema,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(reportNumber, "reportNumber");
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(parameterSchema, "parameterSchema");
        Objects.requireNonNull(now, "now");
        requireNonBlank(name, "name");
        if (name.trim().length() > 200) {
            throw new IllegalArgumentException("name length must be <= 200");
        }
        ReportDefinition definition = new ReportDefinition(
                ReportDefinitionId.generate(),
                tenantId,
                reportNumber,
                code,
                name.trim(),
                blankToNull(description),
                category,
                format,
                parameterSchema,
                ReportDefinitionStatus.DRAFT,
                now,
                null,
                now,
                0L);
        definition.record(new ReportDefinitionRegistered(
                definition.id, tenantId, reportNumber, code, category, now));
        return definition;
    }

    public static ReportDefinition rehydrate(
            ReportDefinitionId id,
            TenantId tenantId,
            ReportNumber reportNumber,
            ReportCode code,
            String name,
            String description,
            ReportCategory category,
            ReportFormat format,
            ParameterSchema parameterSchema,
            ReportDefinitionStatus status,
            Instant registeredAt,
            Instant publishedAt,
            Instant updatedAt,
            long version) {
        return new ReportDefinition(
                id,
                tenantId,
                reportNumber,
                code,
                name,
                description,
                category,
                format,
                parameterSchema,
                status,
                registeredAt,
                publishedAt,
                updatedAt,
                version);
    }

    public void update(
            String name,
            String description,
            ReportCategory category,
            ReportFormat format,
            ParameterSchema parameterSchema,
            Instant now) {
        if (status != ReportDefinitionStatus.DRAFT) {
            throw new DomainException(
                    "DEFINITION_NOT_EDITABLE",
                    "Definition Not Editable",
                    "Only DRAFT report definitions can be updated",
                    409);
        }
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(parameterSchema, "parameterSchema");
        Objects.requireNonNull(now, "now");
        requireNonBlank(name, "name");
        if (name.trim().length() > 200) {
            throw new IllegalArgumentException("name length must be <= 200");
        }
        this.name = name.trim();
        this.description = blankToNull(description);
        this.category = category;
        this.format = format;
        this.parameterSchema = parameterSchema;
        this.updatedAt = now;
        record(new ReportDefinitionUpdated(id, now));
    }

    public void publish(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != ReportDefinitionStatus.DRAFT) {
            throw invalidState("publish");
        }
        this.status = ReportDefinitionStatus.PUBLISHED;
        this.publishedAt = now;
        this.updatedAt = now;
        record(new ReportDefinitionPublished(id, now));
    }

    public void retire(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != ReportDefinitionStatus.PUBLISHED) {
            throw invalidState("retire");
        }
        this.status = ReportDefinitionStatus.RETIRED;
        this.updatedAt = now;
        record(new ReportDefinitionRetired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == ReportDefinitionStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != ReportDefinitionStatus.DRAFT) {
            throw invalidState("cancel");
        }
        this.status = ReportDefinitionStatus.CANCELLED;
        this.updatedAt = now;
        record(new ReportDefinitionCancelled(id, reason.trim(), now));
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_DEFINITION_STATE",
                "Invalid Definition State",
                "Cannot " + action + " report definition in status " + status,
                409);
    }

    private static void requireReason(String reason) {
        requireNonBlank(reason, "reason");
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("reason length must be between 5 and 500");
        }
    }

    private static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void record(ReportDefinitionDomainEvent event) {
        domainEvents.add(event);
    }

    public List<ReportDefinitionDomainEvent> pullDomainEvents() {
        List<ReportDefinitionDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public ReportDefinitionId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public ReportNumber reportNumber() {
        return reportNumber;
    }

    public ReportCode code() {
        return code;
    }

    public String name() {
        return name;
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public ReportCategory category() {
        return category;
    }

    public ReportFormat format() {
        return format;
    }

    public ParameterSchema parameterSchema() {
        return parameterSchema;
    }

    public ReportDefinitionStatus status() {
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
