package et.gov.ethgov.opendata.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.opendata.domain.dataset.DatasetNumber;
import et.gov.ethgov.opendata.domain.dataset.DatasetPublished;
import et.gov.ethgov.opendata.domain.dataset.DatasetRegistered;
import et.gov.ethgov.opendata.domain.dataset.DatasetSlug;
import et.gov.ethgov.opendata.domain.dataset.DatasetStatus;
import et.gov.ethgov.opendata.domain.dataset.DatasetTheme;
import et.gov.ethgov.opendata.domain.dataset.OpenDataset;
import et.gov.ethgov.opendata.domain.dataset.OpenLicense;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class OpenDatasetTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void registersPublishesAndRetires() {
        OpenDataset dataset = register();
        assertThat(dataset.status()).isEqualTo(DatasetStatus.DRAFT);
        assertThat(dataset.pullDomainEvents()).anyMatch(DatasetRegistered.class::isInstance);

        dataset.publish(now.plusSeconds(10));
        assertThat(dataset.status()).isEqualTo(DatasetStatus.PUBLISHED);
        assertThat(dataset.pullDomainEvents()).anyMatch(DatasetPublished.class::isInstance);

        dataset.retire(now.plusSeconds(20));
        assertThat(dataset.status()).isEqualTo(DatasetStatus.RETIRED);
        assertThatThrownBy(() -> dataset.cancel("too late", now.plusSeconds(30)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsInvalidSlug() {
        assertThatThrownBy(() -> DatasetSlug.of("Bad Slug")).isInstanceOf(IllegalArgumentException.class);
    }

    private OpenDataset register() {
        return OpenDataset.register(
                TenantId.generate(),
                DatasetNumber.allocate(2024, 1),
                DatasetSlug.of("land-parcels-addis"),
                "Addis Ababa land parcels",
                "Public land parcel registry extract for Addis Ababa",
                DatasetTheme.LAND,
                OpenLicense.OGL_ETHIOPIA,
                "org_11111111-1111-4111-8111-111111111111",
                "opendata@addis.gov.et",
                "en",
                now);
    }
}
