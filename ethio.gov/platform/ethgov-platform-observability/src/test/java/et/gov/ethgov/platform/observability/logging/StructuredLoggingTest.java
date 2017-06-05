package et.gov.ethgov.platform.observability.logging;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantContext;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class StructuredLoggingTest {

    @AfterEach
    void clear() {
        StructuredLogging.clearRequestFields();
        CorrelationId.clear();
        TenantContext.clear();
    }

    @Test
    void bindsAndClearsMdcFields() {
        CorrelationId.set(CorrelationId.of("corr-log-0001"));
        TenantContext.set(TenantId.generate());
        StructuredLogging.bindRequestFields("ethgov-foundation");
        assertThat(MDC.get(StructuredLogging.SERVICE_NAME)).isEqualTo("ethgov-foundation");
        assertThat(MDC.get(StructuredLogging.CORRELATION_ID)).isEqualTo("corr-log-0001");
        assertThat(MDC.get(StructuredLogging.TENANT_ID)).isNotBlank();
        StructuredLogging.clearRequestFields();
        assertThat(MDC.get(StructuredLogging.SERVICE_NAME)).isNull();
    }
}
