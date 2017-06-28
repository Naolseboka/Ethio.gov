package et.gov.ethgov.platform.web.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ProblemDetailTest {

    @Test
    void buildsProblemDetail() {
        Instant ts = Instant.parse("2024-01-01T00:00:00Z");
        ProblemDetail detail = ProblemDetail.builder()
                .type(URI.create("https://ethgov.et/problems/x"))
                .title("Title")
                .status(400)
                .detail("detail")
                .instance(URI.create("/v1/x"))
                .errorCode("X")
                .correlationId("c1")
                .tenantId("t1")
                .timestamp(ts)
                .extension("k", "v")
                .build();
        assertThat(detail.getTitle()).isEqualTo("Title");
        assertThat(detail.getExtensions()).containsEntry("k", "v");
        assertThat(detail.getTimestamp()).isEqualTo(ts);
        assertThatThrownBy(() -> ProblemDetail.builder().title("t").errorCode("e").status(200).build())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
