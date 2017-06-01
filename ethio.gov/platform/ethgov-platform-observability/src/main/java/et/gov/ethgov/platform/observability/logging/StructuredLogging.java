package et.gov.ethgov.platform.observability.logging;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantContext;
import org.slf4j.MDC;

/**
 * Structured logging field names and MDC helpers shared by all services.
 */
public final class StructuredLogging {

    public static final String CORRELATION_ID = "correlationId";
    public static final String TENANT_ID = "tenantId";
    public static final String SERVICE_NAME = "serviceName";
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";

    private StructuredLogging() {}

    public static void bindRequestFields(String serviceName) {
        MDC.put(SERVICE_NAME, serviceName);
        CorrelationId.current().ifPresent(id -> MDC.put(CORRELATION_ID, id.value()));
        TenantContext.current().ifPresent(id -> MDC.put(TENANT_ID, id.value()));
    }

    public static void clearRequestFields() {
        MDC.remove(SERVICE_NAME);
        MDC.remove(CORRELATION_ID);
        MDC.remove(TENANT_ID);
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
    }
}
