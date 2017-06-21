package et.gov.ethgov.platform.web.filter;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantContext;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Binds correlation and optional tenant identifiers for the request lifecycle.
 */
@Component("ethGovRequestContextFilter")
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class RequestContextFilter extends OncePerRequestFilter {

    public static final String MDC_CORRELATION = "correlationId";
    public static final String MDC_TENANT = "tenantId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            CorrelationId correlationId = resolveCorrelationId(request);
            CorrelationId.set(correlationId);
            MDC.put(MDC_CORRELATION, correlationId.value());
            response.setHeader(CorrelationId.HEADER_NAME, correlationId.value());

            String tenantHeader = request.getHeader(TenantContext.HEADER_NAME);
            if (tenantHeader != null && !tenantHeader.isBlank()) {
                TenantId tenantId = TenantId.of(tenantHeader);
                TenantContext.set(tenantId);
                MDC.put(MDC_TENANT, tenantId.value());
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            CorrelationId.clear();
            MDC.remove(MDC_CORRELATION);
            MDC.remove(MDC_TENANT);
        }
    }

    private CorrelationId resolveCorrelationId(HttpServletRequest request) {
        String header = request.getHeader(CorrelationId.HEADER_NAME);
        if (header == null || header.isBlank()) {
            return CorrelationId.generate();
        }
        return CorrelationId.of(header);
    }
}
