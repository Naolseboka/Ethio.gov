package et.gov.ethgov.platform.web.problem;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.platform.commons.error.IdempotencyConflictException;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class EthGovExceptionHandlerTest {

    private final EthGovExceptionHandler handler = new EthGovExceptionHandler();

    @Test
    void mapsDomainExceptionToProblemJson() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/v1/cases");
        ResponseEntity<ProblemDetail> response =
                handler.handleDomain(new IdempotencyConflictException("duplicate key"), new ServletWebRequest(request));

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getHeaders().getContentType()).hasToString("application/problem+json");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("IDEMPOTENCY_CONFLICT");
        assertThat(response.getBody().getInstance()).isEqualTo(URI.create("/v1/cases"));
    }
}
