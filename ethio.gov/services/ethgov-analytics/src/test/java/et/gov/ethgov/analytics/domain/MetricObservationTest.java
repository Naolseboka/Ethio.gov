package et.gov.ethgov.analytics.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.analytics.domain.observation.MetricObservation;
import et.gov.ethgov.analytics.domain.observation.ObservationNumber;
import et.gov.ethgov.analytics.domain.observation.ObservationRecorded;
import et.gov.ethgov.analytics.domain.observation.ObservationStatus;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class MetricObservationTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void recordsAndVoids() {
        MetricObservation observation = record();
        assertThat(observation.status()).isEqualTo(ObservationStatus.RECORDED);
        assertThat(observation.pullDomainEvents()).anyMatch(ObservationRecorded.class::isInstance);

        observation.voidObservation("incorrect source extract", now.plusSeconds(10));
        assertThat(observation.status()).isEqualTo(ObservationStatus.VOIDED);
    }

    @Test
    void rejectsInvertedPeriod() {
        assertThatThrownBy(() -> MetricObservation.record(
                        TenantId.generate(),
                        ObservationNumber.allocate(2024, 1),
                        MetricDefinitionId.generate(),
                        new BigDecimal("10"),
                        Instant.parse("2024-06-30T00:00:00Z"),
                        Instant.parse("2024-06-01T00:00:00Z"),
                        "{}",
                        null,
                        now))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private MetricObservation record() {
        return MetricObservation.record(
                TenantId.generate(),
                ObservationNumber.allocate(2024, 1),
                MetricDefinitionId.generate(),
                new BigDecimal("42"),
                Instant.parse("2024-05-01T00:00:00Z"),
                Instant.parse("2024-05-31T23:59:59Z"),
                "{\"woreda\":\"bole\"}",
                "rpt_11111111-1111-4111-8111-111111111111",
                now);
    }
}
