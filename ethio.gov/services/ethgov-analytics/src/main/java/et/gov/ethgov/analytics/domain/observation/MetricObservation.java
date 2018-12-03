package et.gov.ethgov.analytics.domain.observation;

import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MetricObservation {
    private final ObservationId id;
    private final TenantId tenantId;
    private final ObservationNumber observationNumber;
    private final MetricDefinitionId metricDefinitionId;
    private final BigDecimal value;
    private final Instant periodStart;
    private final Instant periodEnd;
    private final String dimensionsJson;
    private final String sourceReference;
    private ObservationStatus status;
    private String voidReason;
    private Instant recordedAt;
    private Instant updatedAt;
    private long version;
    private final List<ObservationDomainEvent> domainEvents = new ArrayList<>();

    private MetricObservation(
            ObservationId id,
            TenantId tenantId,
            ObservationNumber observationNumber,
            MetricDefinitionId metricDefinitionId,
            BigDecimal value,
            Instant periodStart,
            Instant periodEnd,
            String dimensionsJson,
            String sourceReference,
            ObservationStatus status,
            String voidReason,
            Instant recordedAt,
            Instant updatedAt,
            long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.observationNumber = observationNumber;
        this.metricDefinitionId = metricDefinitionId;
        this.value = value;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.dimensionsJson = dimensionsJson;
        this.sourceReference = sourceReference;
        this.status = status;
        this.voidReason = voidReason;
        this.recordedAt = recordedAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static MetricObservation record(
            TenantId tenantId,
            ObservationNumber observationNumber,
            MetricDefinitionId metricDefinitionId,
            BigDecimal value,
            Instant periodStart,
            Instant periodEnd,
            String dimensionsJson,
            String sourceReference,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(observationNumber, "observationNumber");
        Objects.requireNonNull(metricDefinitionId, "metricDefinitionId");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(periodStart, "periodStart");
        Objects.requireNonNull(periodEnd, "periodEnd");
        Objects.requireNonNull(now, "now");
        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException("periodEnd must be on or after periodStart");
        }
        if (value.scale() > 6) {
            throw new IllegalArgumentException("value scale must be <= 6");
        }
        if (value.precision() > 18) {
            throw new IllegalArgumentException("value precision must be <= 18");
        }
        MetricObservation observation = new MetricObservation(
                ObservationId.generate(),
                tenantId,
                observationNumber,
                metricDefinitionId,
                value,
                periodStart,
                periodEnd,
                normalizeDimensions(dimensionsJson),
                blankToNull(sourceReference),
                ObservationStatus.RECORDED,
                null,
                now,
                now,
                0L);
        observation.record(new ObservationRecorded(
                observation.id, tenantId, observationNumber, metricDefinitionId, value, now));
        return observation;
    }

    public static MetricObservation rehydrate(
            ObservationId id,
            TenantId tenantId,
            ObservationNumber observationNumber,
            MetricDefinitionId metricDefinitionId,
            BigDecimal value,
            Instant periodStart,
            Instant periodEnd,
            String dimensionsJson,
            String sourceReference,
            ObservationStatus status,
            String voidReason,
            Instant recordedAt,
            Instant updatedAt,
            long version) {
        return new MetricObservation(
                id,
                tenantId,
                observationNumber,
                metricDefinitionId,
                value,
                periodStart,
                periodEnd,
                dimensionsJson,
                sourceReference,
                status,
                voidReason,
                recordedAt,
                updatedAt,
                version);
    }

    public void voidObservation(String reason, Instant now) {
        if (status == ObservationStatus.VOIDED) {
            return;
        }
        Objects.requireNonNull(now, "now");
        requireReason(reason);
        if (status != ObservationStatus.RECORDED) {
            throw new DomainException(
                    "INVALID_OBSERVATION_STATE",
                    "Invalid Observation State",
                    "Cannot void observation in status " + status,
                    409);
        }
        this.status = ObservationStatus.VOIDED;
        this.voidReason = reason.trim();
        this.updatedAt = now;
        record(new ObservationVoided(id, this.voidReason, now));
    }

    private static String normalizeDimensions(String dimensionsJson) {
        if (dimensionsJson == null || dimensionsJson.isBlank()) {
            return "{}";
        }
        String n = dimensionsJson.trim();
        if (!n.startsWith("{") || !n.endsWith("}")) {
            throw new IllegalArgumentException("dimensionsJson must be a JSON object");
        }
        if (n.length() > 4000) {
            throw new IllegalArgumentException("dimensionsJson length must be <= 4000");
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

    private void record(ObservationDomainEvent event) {
        domainEvents.add(event);
    }

    public List<ObservationDomainEvent> pullDomainEvents() {
        List<ObservationDomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    public ObservationId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public ObservationNumber observationNumber() {
        return observationNumber;
    }

    public MetricDefinitionId metricDefinitionId() {
        return metricDefinitionId;
    }

    public BigDecimal value() {
        return value;
    }

    public Instant periodStart() {
        return periodStart;
    }

    public Instant periodEnd() {
        return periodEnd;
    }

    public String dimensionsJson() {
        return dimensionsJson;
    }

    public Optional<String> sourceReference() {
        return Optional.ofNullable(sourceReference);
    }

    public ObservationStatus status() {
        return status;
    }

    public Optional<String> voidReason() {
        return Optional.ofNullable(voidReason);
    }

    public Instant recordedAt() {
        return recordedAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return version;
    }
}
