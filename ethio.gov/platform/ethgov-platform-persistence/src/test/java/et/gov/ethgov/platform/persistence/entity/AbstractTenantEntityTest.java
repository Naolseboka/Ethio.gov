package et.gov.ethgov.platform.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.junit.jupiter.api.Test;

class AbstractTenantEntityTest {

    @Test
    void initializesAuditFields() {
        TestEntity entity = new TestEntity();
        TenantId tenantId = TenantId.generate();
        entity.initialize(tenantId, "system");
        entity.onPersist();

        assertThat(entity.getId()).isNotBlank();
        assertThat(entity.getTenantId()).isEqualTo(tenantId);
        assertThat(entity.getCreatedBy()).isEqualTo("system");
        assertThat(entity.getUpdatedBy()).isEqualTo("system");
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();

        entity.markUpdatedBy("clerk-1");
        entity.onUpdate();
        assertThat(entity.getUpdatedBy()).isEqualTo("clerk-1");
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    static final class TestEntity extends AbstractTenantEntity {}
}
