package et.gov.ethgov.platform.persistence.outbox;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Transactional outbox row written in the same DB transaction as domain changes.
 */
@Entity
@Table(
        name = "platform_outbox",
        indexes = {
            @Index(name = "idx_outbox_status_created", columnList = "status, created_at"),
            @Index(name = "idx_outbox_tenant_created", columnList = "tenant_id, created_at")
        })
public class OutboxMessageEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "tenant_id", nullable = false, length = 40)
    private String tenantId;

    @Column(name = "aggregate_type", nullable = false, length = 128)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 64)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 256)
    private String eventType;

    @Column(name = "topic", nullable = false, length = 256)
    private String topic;

    @Column(name = "partition_key", nullable = false, length = 128)
    private String partitionKey;

    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "correlation_id", nullable = false, length = 128)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OutboxStatus status;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "last_error", length = 2000)
    private String lastError;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected OutboxMessageEntity() {}

    public static OutboxMessageEntity create(
            TenantId tenantId,
            String aggregateType,
            String aggregateId,
            String eventType,
            String topic,
            String partitionKey,
            String payloadJson,
            String correlationId) {
        OutboxMessageEntity entity = new OutboxMessageEntity();
        entity.id = UUID.randomUUID().toString();
        entity.tenantId = Objects.requireNonNull(tenantId, "tenantId").value();
        entity.aggregateType = requireText(aggregateType, "aggregateType");
        entity.aggregateId = requireText(aggregateId, "aggregateId");
        entity.eventType = requireText(eventType, "eventType");
        entity.topic = requireText(topic, "topic");
        entity.partitionKey = requireText(partitionKey, "partitionKey");
        entity.payloadJson = Objects.requireNonNull(payloadJson, "payloadJson");
        entity.correlationId = requireText(correlationId, "correlationId");
        entity.status = OutboxStatus.PENDING;
        entity.attemptCount = 0;
        entity.createdAt = Instant.now();
        return entity;
    }

    public void markPublishing() {
        if (status != OutboxStatus.PENDING && status != OutboxStatus.FAILED) {
            throw new IllegalStateException("Cannot publish outbox message in status " + status);
        }
        this.status = OutboxStatus.PUBLISHING;
        this.attemptCount++;
    }

    public void markPublished() {
        if (status != OutboxStatus.PUBLISHING) {
            throw new IllegalStateException("Cannot mark published from status " + status);
        }
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = Instant.now();
        this.lastError = null;
    }

    public void markFailed(String error) {
        this.status = OutboxStatus.FAILED;
        this.lastError = truncate(error, 2000);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getTopic() {
        return topic;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public String getLastError() {
        return lastError;
    }

    public long getVersion() {
        return version;
    }
}
