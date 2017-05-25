package et.gov.ethgov.platform.messaging.cloudevents;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

/**
 * Builds CloudEvents envelopes with EthGov-required extension attributes.
 */
public final class EthGovCloudEventFactory {

    public static final String EXT_TENANT_ID = "tenantid";
    public static final String EXT_CORRELATION_ID = "correlationid";
    public static final String EXT_AGGREGATE_TYPE = "aggregatetype";
    public static final String EXT_AGGREGATE_ID = "aggregateid";

    private final URI defaultSource;

    public EthGovCloudEventFactory(URI defaultSource) {
        this.defaultSource = Objects.requireNonNull(defaultSource, "defaultSource");
    }

    public CloudEventBuilder builder(
            String eventType,
            TenantId tenantId,
            String aggregateType,
            String aggregateId,
            byte[] data,
            String dataContentType) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(aggregateType, "aggregateType");
        Objects.requireNonNull(aggregateId, "aggregateId");
        Objects.requireNonNull(data, "data");

        String correlation = CorrelationId.current()
                .map(CorrelationId::value)
                .orElseGet(() -> CorrelationId.generate().value());

        return CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType(eventType)
                .withSource(defaultSource)
                .withTime(OffsetDateTime.now(ZoneOffset.UTC))
                .withDataContentType(dataContentType == null ? "application/json" : dataContentType)
                .withData(data)
                .withExtension(EXT_TENANT_ID, tenantId.value())
                .withExtension(EXT_CORRELATION_ID, correlation)
                .withExtension(EXT_AGGREGATE_TYPE, aggregateType)
                .withExtension(EXT_AGGREGATE_ID, aggregateId);
    }

    public static TenantId requireTenant(CloudEvent event) {
        Object value = event.getExtension(EXT_TENANT_ID);
        if (value == null) {
            throw new IllegalArgumentException("CloudEvent missing extension: " + EXT_TENANT_ID);
        }
        return TenantId.of(value.toString());
    }

    public static String requireCorrelationId(CloudEvent event) {
        Object value = event.getExtension(EXT_CORRELATION_ID);
        if (value == null) {
            throw new IllegalArgumentException("CloudEvent missing extension: " + EXT_CORRELATION_ID);
        }
        return value.toString();
    }
}
