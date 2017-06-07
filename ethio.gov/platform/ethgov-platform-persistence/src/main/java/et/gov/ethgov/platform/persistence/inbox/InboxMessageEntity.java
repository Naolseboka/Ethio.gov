package et.gov.ethgov.platform.persistence.inbox;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Inbox deduplication record for consumer-side exactly-once business handling.
 * Uniqueness is per {@code (eventId, consumerName)} so multiple consumers may process the same event.
 */
@Entity
@IdClass(InboxMessageEntity.InboxKey.class)
@Table(
        name = "platform_inbox",
        indexes = {@Index(name = "idx_inbox_tenant_received", columnList = "tenant_id, received_at")})
public class InboxMessageEntity {

    @Id
    @Column(name = "event_id", nullable = false, length = 128)
    private String eventId;

    @Id
    @Column(name = "consumer_name", nullable = false, length = 128)
    private String consumerName;

    @Column(name = "tenant_id", nullable = false, length = 40)
    private String tenantId;

    @Column(name = "event_type", nullable = false, length = 256)
    private String eventType;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected InboxMessageEntity() {}

    public static InboxMessageEntity processed(
            String eventId, TenantId tenantId, String consumerName, String eventType) {
        InboxMessageEntity entity = new InboxMessageEntity();
        entity.eventId = Objects.requireNonNull(eventId, "eventId");
        entity.tenantId = Objects.requireNonNull(tenantId, "tenantId").value();
        entity.consumerName = Objects.requireNonNull(consumerName, "consumerName");
        entity.eventType = Objects.requireNonNull(eventType, "eventType");
        Instant now = Instant.now();
        entity.receivedAt = now;
        entity.processedAt = now;
        return entity;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public static final class InboxKey implements Serializable {
        private String eventId;
        private String consumerName;

        public InboxKey() {}

        public InboxKey(String eventId, String consumerName) {
            this.eventId = eventId;
            this.consumerName = consumerName;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof InboxKey that)) {
                return false;
            }
            return Objects.equals(eventId, that.eventId) && Objects.equals(consumerName, that.consumerName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(eventId, consumerName);
        }
    }
}
