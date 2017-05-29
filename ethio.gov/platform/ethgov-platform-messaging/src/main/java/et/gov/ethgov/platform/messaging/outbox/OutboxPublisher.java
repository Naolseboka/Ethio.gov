package et.gov.ethgov.platform.messaging.outbox;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import java.util.Objects;

/**
 * Application-facing API for appending domain events to the transactional outbox.
 */
public interface OutboxPublisher {

    void append(OutboxAppendCommand command);

    record OutboxAppendCommand(
            TenantId tenantId,
            String aggregateType,
            String aggregateId,
            String eventType,
            String topic,
            String partitionKey,
            String payloadJson) {

        public OutboxAppendCommand {
            Objects.requireNonNull(tenantId, "tenantId");
            Objects.requireNonNull(aggregateType, "aggregateType");
            Objects.requireNonNull(aggregateId, "aggregateId");
            Objects.requireNonNull(eventType, "eventType");
            Objects.requireNonNull(topic, "topic");
            Objects.requireNonNull(partitionKey, "partitionKey");
            Objects.requireNonNull(payloadJson, "payloadJson");
        }

        public OutboxMessageEntity toEntity() {
            String correlation = CorrelationId.current()
                    .map(CorrelationId::value)
                    .orElseGet(() -> CorrelationId.generate().value());
            return OutboxMessageEntity.create(
                    tenantId,
                    aggregateType,
                    aggregateId,
                    eventType,
                    topic,
                    partitionKey,
                    payloadJson,
                    correlation);
        }
    }
}
