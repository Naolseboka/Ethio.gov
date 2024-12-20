package et.gov.ethgov.reporting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionId;
import et.gov.ethgov.reporting.domain.run.ReportRun;
import et.gov.ethgov.reporting.domain.run.ReportRunRequested;
import et.gov.ethgov.reporting.domain.run.ReportRunStatus;
import et.gov.ethgov.reporting.domain.run.RunNumber;
import et.gov.ethgov.reporting.domain.run.RunParameters;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ReportRunTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void queuesStartsAndSucceeds() {
        ReportRun run = request();
        assertThat(run.status()).isEqualTo(ReportRunStatus.QUEUED);
        assertThat(run.pullDomainEvents()).anyMatch(ReportRunRequested.class::isInstance);

        run.start(now.plusSeconds(5));
        assertThat(run.status()).isEqualTo(ReportRunStatus.RUNNING);

        run.succeed("doc_11111111-1111-4111-8111-111111111111", now.plusSeconds(10));
        assertThat(run.status()).isEqualTo(ReportRunStatus.SUCCEEDED);
        assertThatThrownBy(() -> run.cancel("too late", now.plusSeconds(20))).isInstanceOf(DomainException.class);
    }

    @Test
    void cancelsQueuedOnly() {
        ReportRun run = request();
        run.cancel("requester withdrew request", now.plusSeconds(2));
        assertThat(run.status()).isEqualTo(ReportRunStatus.CANCELLED);
    }

    private ReportRun request() {
        return ReportRun.request(
                TenantId.generate(),
                RunNumber.allocate(2024, 1),
                ReportDefinitionId.generate(),
                RunParameters.of("{\"month\":\"2024-05\"}"),
                Instant.parse("2024-05-01T00:00:00Z"),
                Instant.parse("2024-05-31T23:59:59Z"),
                "usr_11111111-1111-4111-8111-111111111111",
                now);
    }
}
