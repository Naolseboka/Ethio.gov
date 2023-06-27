package et.gov.ethgov.opendata.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.opendata.domain.dataset.DatasetId;
import et.gov.ethgov.opendata.domain.resource.DatasetResource;
import et.gov.ethgov.opendata.domain.resource.ResourceAdded;
import et.gov.ethgov.opendata.domain.resource.ResourceFormat;
import et.gov.ethgov.opendata.domain.resource.ResourceNumber;
import et.gov.ethgov.opendata.domain.resource.ResourceStatus;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DatasetResourceTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void addsAndRetiresResource() {
        DatasetResource resource = add();
        assertThat(resource.status()).isEqualTo(ResourceStatus.ACTIVE);
        assertThat(resource.pullDomainEvents()).anyMatch(ResourceAdded.class::isInstance);

        resource.retire("file superseded", now.plusSeconds(10));
        assertThat(resource.status()).isEqualTo(ResourceStatus.RETIRED);
        assertThatThrownBy(() -> resource.update(
                        "x", null, ResourceFormat.CSV, "https://example.et/a.csv", null, null, now.plusSeconds(20)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void requiresAccessUrlOrDocumentId() {
        assertThatThrownBy(() -> DatasetResource.add(
                        TenantId.generate(),
                        ResourceNumber.allocate(2024, 1),
                        DatasetId.generate(),
                        "Parcels CSV",
                        null,
                        ResourceFormat.CSV,
                        null,
                        null,
                        null,
                        now))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private DatasetResource add() {
        return DatasetResource.add(
                TenantId.generate(),
                ResourceNumber.allocate(2024, 1),
                DatasetId.generate(),
                "Parcels CSV",
                "Monthly extract",
                ResourceFormat.CSV,
                "https://opendata.addis.gov.et/parcels.csv",
                null,
                "text/csv",
                now);
    }
}
