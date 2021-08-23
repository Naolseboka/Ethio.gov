package et.gov.ethgov.gis.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.gis.domain.feature.FeatureNumber;
import et.gov.ethgov.gis.domain.feature.FeaturePublished;
import et.gov.ethgov.gis.domain.feature.FeatureRegistered;
import et.gov.ethgov.gis.domain.feature.FeatureStatus;
import et.gov.ethgov.gis.domain.feature.FeatureType;
import et.gov.ethgov.gis.domain.feature.GisFeature;
import et.gov.ethgov.gis.domain.feature.WktGeometry;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class GisFeatureTest {

    private static final String ADDIS_POLYGON =
            "POLYGON((38.73 9.02,38.75 9.02,38.75 9.04,38.73 9.04,38.73 9.02))";
    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void registersPublishesAndRetires() {
        GisFeature feature = registerZone();
        assertThat(feature.status()).isEqualTo(FeatureStatus.DRAFT);
        assertThat(feature.pullDomainEvents()).anyMatch(FeatureRegistered.class::isInstance);

        feature.publish(now.plusSeconds(10));
        assertThat(feature.status()).isEqualTo(FeatureStatus.PUBLISHED);
        assertThat(feature.pullDomainEvents()).anyMatch(FeaturePublished.class::isInstance);

        feature.retire(now.plusSeconds(20));
        assertThat(feature.status()).isEqualTo(FeatureStatus.RETIRED);
        assertThatThrownBy(() -> feature.cancel("cannot cancel retired", now.plusSeconds(30)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void requiresParcelIdForParcelBoundary() {
        assertThatThrownBy(() -> GisFeature.register(
                        TenantId.generate(),
                        FeatureNumber.allocate(2024, 1),
                        FeatureType.PARCEL_BOUNDARY,
                        "Parcel A",
                        null,
                        WktGeometry.of(ADDIS_POLYGON),
                        null,
                        null,
                        now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("parcelId");
    }

    @Test
    void cancelsDraftOnly() {
        GisFeature feature = registerZone();
        feature.cancel("draft abandoned by surveyor", now.plusSeconds(5));
        assertThat(feature.status()).isEqualTo(FeatureStatus.CANCELLED);
    }

    @Test
    void rejectsInvalidWkt() {
        assertThatThrownBy(() -> WktGeometry.of("NOTAGEOMETRY"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private GisFeature registerZone() {
        return GisFeature.register(
                TenantId.generate(),
                FeatureNumber.allocate(2024, 1),
                FeatureType.ZONE,
                "Bole Zone",
                "Municipal planning zone",
                WktGeometry.of(ADDIS_POLYGON),
                null,
                null,
                now);
    }
}
