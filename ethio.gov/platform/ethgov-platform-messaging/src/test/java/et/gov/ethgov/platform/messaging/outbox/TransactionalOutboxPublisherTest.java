package et.gov.ethgov.platform.messaging.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import et.gov.ethgov.platform.persistence.outbox.OutboxStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TransactionalOutboxPublisherTest {

    @AfterEach
    void cleanup() {
        CorrelationId.clear();
    }

    @Test
    void appendsAndFlushesPendingMessages() {
        List<OutboxMessageEntity> written = new ArrayList<>();
        TransactionalOutboxPublisher publisher = new TransactionalOutboxPublisher(written::addAll);
        CorrelationId.set(CorrelationId.of("corr-test-0001"));
        TenantId tenantId = TenantId.generate();

        publisher.append(new OutboxPublisher.OutboxAppendCommand(
                tenantId,
                "Citizen",
                "cit-1",
                "et.gov.ethgov.citizen.registered.v1",
                "ethgov.citizen.events",
                "cit-1",
                "{\"id\":\"cit-1\"}"));
        publisher.flush();

        assertThat(written).hasSize(1);
        OutboxMessageEntity message = written.getFirst();
        assertThat(message.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(message.getTenantId()).isEqualTo(tenantId.value());
        assertThat(message.getCorrelationId()).isEqualTo("corr-test-0001");
        assertThat(message.getEventType()).isEqualTo("et.gov.ethgov.citizen.registered.v1");
    }
}
