package et.gov.ethgov.gis.domain.feature;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class GisFeature {
    private final FeatureId id;
    private final TenantId tenantId;
    private final FeatureNumber featureNumber;
    private FeatureType type;
    private FeatureStatus status;
    private String name;
    private String description;
    private WktGeometry geometry;
    private String parcelId;
    private String permitId;
    private Instant registeredAt;
    private Instant publishedAt;
    private Instant updatedAt;
    private long version;
    private final List<FeatureDomainEvent> domainEvents = new ArrayList<>();

    private GisFeature(
            FeatureId id,
            TenantId tenantId,
            FeatureNumber featureNumber,
            FeatureType type,
            FeatureStatus status,
            String name,
            String description,
            WktGeometry geometry,
            String parcelId,
            String permitId,
            Instant registeredAt,
            Instant publishedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.featureNumber = featureNumber;
        this.type = type;
        this.status = status;
        this.name = name;
        this.description = description;
        this.geometry = geometry;
        this.parcelId = parcelId;
        this.permitId = permitId;
        this.registeredAt = registeredAt;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static GisFeature register(
            TenantId tenantId,
            FeatureNumber featureNumber,
            FeatureType type,
            String name,
            String description,
            WktGeometry geometry,
            String parcelId,
            String permitId,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(featureNumber, "featureNumber");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(geometry, "geometry");
        Objects.requireNonNull(now, "now");
        requireNonBlank(name, "name");
        if (name.trim().length() > 200) {
            throw new IllegalArgumentException("name length must be <= 200");
        }
        validateOptionalId(parcelId, "par_", "parcelId");
        validateOptionalId(permitId, "prm_", "permitId");
        if (type == FeatureType.PARCEL_BOUNDARY && (parcelId == null || parcelId.isBlank())) {
            throw new IllegalArgumentException("parcelId is required for PARCEL_BOUNDARY features");
        }
        GisFeature feature = new GisFeature(
                FeatureId.generate(),
                tenantId,
                featureNumber,
                type,
                FeatureStatus.DRAFT,
                name.trim(),
                blankToNull(description),
                geometry,
                blankToNull(parcelId),
                blankToNull(permitId),
                now,
                null,
                now,
                0L);
        feature.record(new FeatureRegistered(feature.id, tenantId, featureNumber, type, now));
        return feature;
    }

    public static GisFeature rehydrate(
            FeatureId id,
            TenantId tenantId,
            FeatureNumber featureNumber,
            FeatureType type,
            FeatureStatus status,
            String name,
            String description,
            WktGeometry geometry,
            String parcelId,
            String permitId,
            Instant registeredAt,
            Instant publishedAt,
            Instant updatedAt,
            long version) {
        return new GisFeature(
                id, tenantId, featureNumber, type, status, name, description, geometry,
                parcelId, permitId, registeredAt, publishedAt, updatedAt, version);
    }

    public void update(
            String name, String description, WktGeometry geometry, String parcelId, String permitId, Instant now) {
        if (status != FeatureStatus.DRAFT) {
            throw new DomainException(
                    "FEATURE_NOT_EDITABLE",
                    "Feature Not Editable",
                    "Only DRAFT features can be updated",
                    409);
        }
        Objects.requireNonNull(geometry, "geometry");
        Objects.requireNonNull(now, "now");
        requireNonBlank(name, "name");
        if (name.trim().length() > 200) {
            throw new IllegalArgumentException("name length must be <= 200");
        }
        validateOptionalId(parcelId, "par_", "parcelId");
        validateOptionalId(permitId, "prm_", "permitId");
        if (type == FeatureType.PARCEL_BOUNDARY && (parcelId == null || parcelId.isBlank())) {
            throw new IllegalArgumentException("parcelId is required for PARCEL_BOUNDARY features");
        }
        this.name = name.trim();
        this.description = blankToNull(description);
        this.geometry = geometry;
        this.parcelId = blankToNull(parcelId);
        this.permitId = blankToNull(permitId);
        this.updatedAt = now;
        record(new FeatureUpdated(id, now));
    }

    public void publish(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != FeatureStatus.DRAFT) {
            throw invalidState("publish");
        }
        this.status = FeatureStatus.PUBLISHED;
        this.publishedAt = now;
        this.updatedAt = now;
        record(new FeaturePublished(id, now));
    }

    public void retire(Instant now) {
        Objects.requireNonNull(now, "now");
        if (status != FeatureStatus.PUBLISHED) {
            throw invalidState("retire");
        }
        this.status = FeatureStatus.RETIRED;
        this.updatedAt = now;
        record(new FeatureRetired(id, now));
    }

    public void cancel(String reason, Instant now) {
        if (status == FeatureStatus.CANCELLED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != FeatureStatus.DRAFT) {
            throw invalidState("cancel");
        }
        this.status = FeatureStatus.CANCELLED;
        this.updatedAt = now;
        record(new FeatureCancelled(id, reason.trim(), now));
    }

    private DomainException invalidState(String action) {
        return new DomainException(
                "INVALID_FEATURE_STATE",
                "Invalid Feature State",
                "Cannot " + action + " feature in status " + status,
                409);
    }

    private static void requireReason(String reason) {
        requireNonBlank(reason, "reason");
        if (reason.trim().length() < 5 || reason.trim().length() > 500) {
            throw new IllegalArgumentException("reason length must be between 5 and 500");
        }
    }

    private static void validateOptionalId(String value, String prefix, String field) {
        if (value == null || value.isBlank()) return;
        String n = value.trim();
        if (!n.startsWith(prefix) || n.length() < 40) {
            throw new IllegalArgumentException(field + " must be a valid " + prefix + " identifier");
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

    private void record(FeatureDomainEvent event) {
        domainEvents.add(event);
    }

    public List<FeatureDomainEvent> pullDomainEvents() {
        List<FeatureDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public FeatureId id() { return id; }
    public TenantId tenantId() { return tenantId; }
    public FeatureNumber featureNumber() { return featureNumber; }
    public FeatureType type() { return type; }
    public FeatureStatus status() { return status; }
    public String name() { return name; }
    public Optional<String> description() { return Optional.ofNullable(description); }
    public WktGeometry geometry() { return geometry; }
    public Optional<String> parcelId() { return Optional.ofNullable(parcelId); }
    public Optional<String> permitId() { return Optional.ofNullable(permitId); }
    public Instant registeredAt() { return registeredAt; }
    public Optional<Instant> publishedAt() { return Optional.ofNullable(publishedAt); }
    public Instant updatedAt() { return updatedAt; }
    public long entityVersion() { return version; }
}
