package et.gov.ethgov.platform.persistence.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.junit.jupiter.api.Test;

class OutboxMessageEntityTest {

    @Test
    void followsPublishStateMachine() {
        OutboxMessageEntity message = OutboxMessageEntity.create(
                TenantId.generate(),
                "Case",
                "case-1",
                "et.gov.ethgov.case.opened.v1",
                "ethgov.case.events",
                "case-1",
                "{\"id\":\"case-1\"}",
                "corr-1");

        assertThat(message.getStatus()).isEqualTo(OutboxStatus.PENDING);
        message.markPublishing();
        assertThat(message.getStatus()).isEqualTo(OutboxStatus.PUBLISHING);
        assertThat(message.getAttemptCount()).isEqualTo(1);
        message.markPublished();
        assertThat(message.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
        assertThat(message.getPublishedAt()).isNotNull();
    }

    @Test
    void rejectsInvalidTransitions() {
        OutboxMessageEntity message = OutboxMessageEntity.create(
                TenantId.generate(),
                "Case",
                "case-1",
                "et.gov.ethgov.case.opened.v1",
                "ethgov.case.events",
                "case-1",
                "{}",
                "corr-1");
        assertThatThrownBy(message::markPublished).isInstanceOf(IllegalStateException.class);
        message.markPublishing();
        message.markFailed("broker timeout");
        assertThat(message.getStatus()).isEqualTo(OutboxStatus.FAILED);
        assertThat(message.getLastError()).contains("broker timeout");
        message.markPublishing();
        assertThat(message.getAttemptCount()).isEqualTo(2);
    }
}
