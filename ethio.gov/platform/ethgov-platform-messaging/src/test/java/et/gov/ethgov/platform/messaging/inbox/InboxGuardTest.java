package et.gov.ethgov.platform.messaging.inbox;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.persistence.inbox.InboxMessageEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class InboxGuardTest {

    @Test
    void processesOnlyOncePerConsumer() {
        Set<String> keys = new HashSet<>();
        InboxGuard guard = new InboxGuard(new InboxGuard.InboxStore() {
            @Override
            public boolean exists(String eventId, String consumerName) {
                return keys.contains(eventId + "|" + consumerName);
            }

            @Override
            public void save(InboxMessageEntity entity) {
                keys.add(entity.getEventId() + "|" + entity.getConsumerName());
            }
        });

        AtomicInteger counter = new AtomicInteger();
        TenantId tenantId = TenantId.generate();
        boolean first = guard.processOnce("evt-1", tenantId, "billing", "payment.completed", counter::incrementAndGet);
        boolean second = guard.processOnce("evt-1", tenantId, "billing", "payment.completed", counter::incrementAndGet);

        assertThat(first).isTrue();
        assertThat(second).isFalse();
        assertThat(counter.get()).isEqualTo(1);
    }
}
