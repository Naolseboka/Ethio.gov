package et.gov.ethgov.platform.messaging.cloudevents;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import io.cloudevents.CloudEvent;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class EthGovCloudEventFactoryTest {

    @AfterEach
    void clear() {
        CorrelationId.clear();
    }

    @Test
    void buildsEventWithRequiredExtensions() {
        CorrelationId.set(CorrelationId.of("corr-cloud-01"));
        TenantId tenantId = TenantId.generate();
        EthGovCloudEventFactory factory =
                new EthGovCloudEventFactory(URI.create("https://ethgov.et/services/foundation"));

        CloudEvent event = factory.builder(
                        "et.gov.ethgov.foundation.probe.v1",
                        tenantId,
                        "Probe",
                        "probe-1",
                        "{\"ok\":true}".getBytes(StandardCharsets.UTF_8),
                        "application/json")
                .build();

        assertThat(EthGovCloudEventFactory.requireTenant(event)).isEqualTo(tenantId);
        assertThat(EthGovCloudEventFactory.requireCorrelationId(event)).isEqualTo("corr-cloud-01");
        assertThat(event.getType()).isEqualTo("et.gov.ethgov.foundation.probe.v1");
        assertThat(event.getExtension(EthGovCloudEventFactory.EXT_AGGREGATE_ID)).isEqualTo("probe-1");
    }
}
