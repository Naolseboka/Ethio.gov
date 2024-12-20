package et.gov.ethgov.reporting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.reporting.domain.definition.ParameterSchema;
import et.gov.ethgov.reporting.domain.definition.ReportCategory;
import et.gov.ethgov.reporting.domain.definition.ReportCode;
import et.gov.ethgov.reporting.domain.definition.ReportDefinition;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionPublished;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionRegistered;
import et.gov.ethgov.reporting.domain.definition.ReportDefinitionStatus;
import et.gov.ethgov.reporting.domain.definition.ReportFormat;
import et.gov.ethgov.reporting.domain.definition.ReportNumber;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ReportDefinitionTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void registersPublishesAndRetires() {
        ReportDefinition definition = register();
        assertThat(definition.status()).isEqualTo(ReportDefinitionStatus.DRAFT);
        assertThat(definition.pullDomainEvents()).anyMatch(ReportDefinitionRegistered.class::isInstance);

        definition.publish(now.plusSeconds(10));
        assertThat(definition.status()).isEqualTo(ReportDefinitionStatus.PUBLISHED);
        assertThat(definition.pullDomainEvents()).anyMatch(ReportDefinitionPublished.class::isInstance);

        definition.retire(now.plusSeconds(20));
        assertThat(definition.status()).isEqualTo(ReportDefinitionStatus.RETIRED);
        assertThatThrownBy(() -> definition.cancel("cannot cancel retired", now.plusSeconds(30)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsInvalidCode() {
        assertThatThrownBy(() -> ReportCode.of("bad-code")).isInstanceOf(IllegalArgumentException.class);
    }

    private ReportDefinition register() {
        return ReportDefinition.register(
                TenantId.generate(),
                ReportNumber.allocate(2024, 1),
                ReportCode.of("LICENSE_ISSUED_MONTHLY"),
                "Licenses issued monthly",
                "Municipal license issuance summary",
                ReportCategory.LICENSE,
                ReportFormat.CSV,
                ParameterSchema.of("{\"period\":\"month\"}"),
                now);
    }
}
