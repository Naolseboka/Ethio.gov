package et.gov.ethgov.analytics.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.analytics.domain.metric.AggregationType;
import et.gov.ethgov.analytics.domain.metric.MetricCategory;
import et.gov.ethgov.analytics.domain.metric.MetricCode;
import et.gov.ethgov.analytics.domain.metric.MetricDefinition;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionPublished;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionRegistered;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionStatus;
import et.gov.ethgov.analytics.domain.metric.MetricNumber;
import et.gov.ethgov.analytics.domain.metric.MetricUnit;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class MetricDefinitionTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void registersPublishesAndRetires() {
        MetricDefinition definition = register();
        assertThat(definition.status()).isEqualTo(MetricDefinitionStatus.DRAFT);
        assertThat(definition.pullDomainEvents()).anyMatch(MetricDefinitionRegistered.class::isInstance);

        definition.publish(now.plusSeconds(10));
        assertThat(definition.status()).isEqualTo(MetricDefinitionStatus.PUBLISHED);
        assertThat(definition.pullDomainEvents()).anyMatch(MetricDefinitionPublished.class::isInstance);

        definition.retire(now.plusSeconds(20));
        assertThat(definition.status()).isEqualTo(MetricDefinitionStatus.RETIRED);
        assertThatThrownBy(() -> definition.cancel("too late", now.plusSeconds(30)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsInvalidCode() {
        assertThatThrownBy(() -> MetricCode.of("bad-code")).isInstanceOf(IllegalArgumentException.class);
    }

    private MetricDefinition register() {
        return MetricDefinition.register(
                TenantId.generate(),
                MetricNumber.allocate(2024, 1),
                MetricCode.of("LICENSE_ISSUED_COUNT"),
                "Licenses issued",
                "Count of business licenses issued in period",
                MetricCategory.LICENSE,
                MetricUnit.COUNT,
                AggregationType.SUM,
                now);
    }
}
