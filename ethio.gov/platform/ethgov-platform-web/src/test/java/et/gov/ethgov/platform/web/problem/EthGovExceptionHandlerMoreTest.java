package et.gov.ethgov.platform.web.problem;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.platform.commons.error.DomainException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class EthGovExceptionHandlerMoreTest {

    private final EthGovExceptionHandler handler = new EthGovExceptionHandler();

    @Test
    void mapsIllegalArgumentAndUnhandled() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/x");
        ServletWebRequest webRequest = new ServletWebRequest(request);

        ResponseEntity<ProblemDetail> badRequest =
                handler.handleIllegalArgument(new IllegalArgumentException("bad"), webRequest);
        assertThat(badRequest.getStatusCode().value()).isEqualTo(400);
        assertThat(badRequest.getBody().getErrorCode()).isEqualTo("BAD_REQUEST");

        ResponseEntity<ProblemDetail> internal =
                handler.handleUnhandled(new RuntimeException("boom"), webRequest);
        assertThat(internal.getStatusCode().value()).isEqualTo(500);
        assertThat(internal.getBody().getDetail()).isEqualTo("An unexpected error occurred");

        DomainException domain = new DomainException("X", "Title", "detail", 422, Map.of("k", "v"));
        ResponseEntity<ProblemDetail> mapped = handler.handleDomain(domain, webRequest);
        assertThat(mapped.getStatusCode().value()).isEqualTo(422);
        assertThat(mapped.getBody().getExtensions()).containsEntry("k", "v");
    }
}
