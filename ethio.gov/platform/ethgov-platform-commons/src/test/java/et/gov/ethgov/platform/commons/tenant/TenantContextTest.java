package et.gov.ethgov.platform.commons.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TenantContextTest {

    @AfterEach
    void clear() {
        TenantContext.clear();
    }

    @Test
    void bindsAndClearsTenant() {
        TenantId tenantId = TenantId.generate();
        assertThat(TenantContext.current()).isEmpty();
        TenantContext.set(tenantId);
        assertThat(TenantContext.require()).isEqualTo(tenantId);
        TenantContext.clear();
        assertThatThrownBy(TenantContext::require).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void callWithRestoresPrevious() {
        TenantId outer = TenantId.generate();
        TenantId inner = TenantId.generate();
        TenantContext.set(outer);
        String result = TenantContext.callWith(inner, () -> {
            assertThat(TenantContext.require()).isEqualTo(inner);
            return "ok";
        });
        assertThat(result).isEqualTo("ok");
        assertThat(TenantContext.require()).isEqualTo(outer);
    }

    @Test
    void rejectsMalformedId() {
        assertThatThrownBy(() -> TenantId.of("not-a-tenant"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
