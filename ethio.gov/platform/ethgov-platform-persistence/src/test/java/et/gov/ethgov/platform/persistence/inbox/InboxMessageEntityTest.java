package et.gov.ethgov.platform.persistence.inbox;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.junit.jupiter.api.Test;

class InboxMessageEntityTest {

    @Test
    void createsProcessedRecord() {
        TenantId tenantId = TenantId.generate();
        InboxMessageEntity entity =
                InboxMessageEntity.processed("evt-9", tenantId, "revenue", "payment.completed");
        assertThat(entity.getEventId()).isEqualTo("evt-9");
        assertThat(entity.getConsumerName()).isEqualTo("revenue");
        assertThat(entity.getTenantId()).isEqualTo(tenantId.value());
        assertThat(entity.getProcessedAt()).isNotNull();
    }
}
