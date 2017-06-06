package et.gov.ethgov.platform.observability.tracing;

import static org.assertj.core.api.Assertions.assertThatCode;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantContext;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class EthGovSpanEnricherTest {

    @AfterEach
    void clear() {
        CorrelationId.clear();
        TenantContext.clear();
    }

    @Test
    void enrichIsSafeWithoutActiveSpan() {
        CorrelationId.set(CorrelationId.of("corr-span-0001"));
        TenantContext.set(TenantId.generate());
        EthGovSpanEnricher enricher = new EthGovSpanEnricher("foundation");
        assertThatCode(enricher::enrichCurrentSpan).doesNotThrowAnyException();
    }
}
