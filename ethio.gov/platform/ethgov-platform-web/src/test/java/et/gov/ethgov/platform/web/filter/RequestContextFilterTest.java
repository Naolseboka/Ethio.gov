package et.gov.ethgov.platform.web.filter;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.platform.commons.correlation.CorrelationId;
import et.gov.ethgov.platform.commons.tenant.TenantContext;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestContextFilterTest {

    private final RequestContextFilter filter = new RequestContextFilter();

    @AfterEach
    void clear() {
        TenantContext.clear();
        CorrelationId.clear();
    }

    @Test
    void bindsTenantAndCorrelationFromHeaders() throws ServletException, IOException {
        TenantId tenantId = TenantId.generate();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationId.HEADER_NAME, "corr-filter-001");
        request.addHeader(TenantContext.HEADER_NAME, tenantId.value());
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader(CorrelationId.HEADER_NAME)).isEqualTo("corr-filter-001");
        assertThat(TenantContext.current()).isEmpty();
        assertThat(CorrelationId.current()).isEmpty();
    }

    @Test
    void generatesCorrelationWhenMissing() throws ServletException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(), response, new MockFilterChain());
        assertThat(response.getHeader(CorrelationId.HEADER_NAME)).isNotBlank();
    }
}
