package et.gov.ethgov.analytics.domain.metric;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MetricDefinition {
    private final MetricDefinitionId id;
    private final TenantId tenantId;
    private final MetricNumber metricNumber;
    private final MetricCode code;
    private String name;
    private String description;
    private MetricCategory category;
    private MetricUnit unit;
    private AggregationType aggregation;
    private MetricDefinitionStatus status;
    private Instant registeredAt;
    private Instant publishedAt;
    private Instant updatedAt;
    private long version;
    private final List<MetricDefinitionDomainEvent> domainEvents = new ArrayList<>();

    private MetricDefinition(
            MetricDefinitionId id,
            TenantId tenantId,
            MetricNumber metricNumber,
            MetricCode code,
            String name,
            String description,
            MetricCategory category,
            MetricUnit unit,
            AggregationType aggregation,
            MetricDefinitionStatus status,
            Instant registeredAt,
            Instant publishedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.metricNumber = metricNumber;
        this.code = code;
        this.name = name;
        this.description = description;
        this.category = category;
        this.unit = unit;
        this.aggregation = aggregation;
        this.status = status;
        this.registeredAt = registeredAt;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static MetricDefinition register(
            TenantId tenantId,
            MetricNumber metricNumber,
            MetricCode code,
            String name,
            String description,
            MetricCategory category,
            MetricUnit unit,
            AggregationType aggregation,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(metricNumber, "metricNumber");
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(unit, "unit");
        Objects.requireNonNull(aggregation, "aggregation");
        Objects.requireNonNull(now, "now");
        MetricDefinition definition = new MetricDefinition(
                MetricDefinitionId.generate(),
                tenantId,
                metricNumber,
                code,
                requireName(name),
                blankToNull(description),
                category,
                unit,
                aggregation,
                MetricDefinitionStatus.DRAFT,
                now,
                null,
                now,
                0L);
        definition.record(new MetricDefinitionRegistered(
                definition.id, tenantId, metricNumber, code, category, now));
        return definition;
    }

    public static MetricDefinition rehydrate(
            MetricDefinitionId id,
            TenantId tenantId,
            MetricNumber metricNumber,
            MetricCode code,
            String name,
            String description,
            MetricCategory category,
            MetricUnit unit,
            AggregationType aggregation,
            MetricDefinitionStatus status,
            Instant registeredAt,
            Instant publishedAt,
            Instant updatedAt,
            long version) {
        return new MetricDefinition(
                id,
                tenantId,
                metricNumber,
                code,
                name,
                description,
                category,
                unit,
                aggregation,
                status,
                registeredAt,
                publishedAt,
                updatedAt,
                version);
    }

    public void update(
            String name,
            String description,
            MetricCategory category,
            MetricUnit unit,
            AggregationType aggregation,
            Instant now) {
        if (status != MetricDefinitionStatus.DRAFT) {
            throw new DomainException(
                    "METRIC_NOT_EDITABLE",
                    "Metric Not Editable",
                    "Only DRAFT metric definitions can be updated",
                    409);
        }
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(unit, "unit");
        Objects.requireNonNull(aggregation, "aggregation");
        Objects.requireNonNull(now, "now");
        this.name = requireName(name);
        this.description = blankToNull(description);
        this.category = category;
        this.unit = unit;
        this.aggregation = aggregation;
        this.updatedAt = now;
        record(new MetricDefinitionUpdated(id, now));
    }

    public void publish(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != MetricDefinitionStatus.DRAFT) {
            throw invalidState("publish");
        }
        this.status = MetricDefinitionStatus.PUBLISHED;
        this.publishedAt = now;
        this.updatedAt = now;
        record(new MetricDefinitionPublished(id, now));
    }

    public void retire(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != MetricDefinitionStatus.PUBLISHED) {
            throw invalidState("retire");
        }
        this.status = MetricDefinitionStatus.RETIRED;
        this.updatedAt = now;
        record(new MetricDefinitionRetired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == MetricDefinitionStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != MetricDefinitionStatus.DRAFT) {
            throw invalidState("cancel");
        }
        this.status = MetricDefinitionStatus.CANCELLED;
        this.updatedAt = now;
        record(new MetricDefinitionCancelled(id, reason.trim(), now));
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_METRIC_STATE",
                "Invalid Metric State",
                "Cannot " + action + " metric definition in status " + status,
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

    private void record(MetricDefinitionDomainEvent event) {
        domainEvents.add(event);
    }

    public List<MetricDefinitionDomainEvent> pullDomainEvents() {
        List<MetricDefinitionDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public MetricDefinitionId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public MetricNumber metricNumber() {
        return metricNumber;
    }

    public MetricCode code() {
        return code;
    }

    public String name() {
        return name;
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public MetricCategory category() {
        return category;
    }

    public MetricUnit unit() {
        return unit;
    }

    public AggregationType aggregation() {
        return aggregation;
    }

    public MetricDefinitionStatus status() {
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
