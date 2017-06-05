package et.gov.ethgov.platform.observability.tracing;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantContext;
import io.opentelemetry.api.trace.Span;
import java.util.Objects;

/**
 * Applies EthGov semantic attributes onto the current OpenTelemetry span.
 */
public final class EthGovSpanEnricher {

    public static final String ATTR_TENANT_ID = "ethgov.tenant_id";
    public static final String ATTR_CORRELATION_ID = "ethgov.correlation_id";
    public static final String ATTR_SERVICE_DOMAIN = "ethgov.service_domain";

    private final String serviceDomain;

    public EthGovSpanEnricher(String serviceDomain) {
        this.serviceDomain = Objects.requireNonNull(serviceDomain, "serviceDomain");
    }

    public void enrichCurrentSpan() {
        Span span = Span.current();
        if (!span.getSpanContext().isValid()) {
            return;
        }
        span.setAttribute(ATTR_SERVICE_DOMAIN, serviceDomain);
        TenantContext.current().ifPresent(tenantId -> span.setAttribute(ATTR_TENANT_ID, tenantId.value()));
        CorrelationId.current()
                .ifPresent(correlationId -> span.setAttribute(ATTR_CORRELATION_ID, correlationId.value()));
    }
}
