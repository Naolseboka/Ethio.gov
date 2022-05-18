package et.gov.ethgov.land.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.land.domain.parcel.AreaSquareMeters;
import et.gov.ethgov.land.domain.parcel.LandParcel;
import et.gov.ethgov.land.domain.parcel.LandUse;
import et.gov.ethgov.land.domain.parcel.ParcelNumber;
import et.gov.ethgov.land.domain.parcel.ParcelOwner;
import et.gov.ethgov.land.domain.parcel.ParcelRegistered;
import et.gov.ethgov.land.domain.parcel.ParcelStatus;
import et.gov.ethgov.land.domain.parcel.TenureType;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class LandParcelTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final String citizen = "cit_11111111-1111-4111-8111-111111111111";
    private final String org = "org_22222222-2222-4222-8222-222222222222";
    private final String doc = "doc_11111111-1111-4111-8111-111111111111";

    @Test
    void registersTransfersEncumbersAndClears() {
        LandParcel parcel = register();
        assertThat(parcel.status()).isEqualTo(ParcelStatus.ACTIVE);
        assertThat(parcel.pullDomainEvents()).anyMatch(ParcelRegistered.class::isInstance);

        parcel.transfer(ParcelOwner.of(null, org), now);
        assertThat(parcel.owner().organizationId()).contains(org);

        parcel.encumber("Bank mortgage lien", now);
        assertThat(parcel.status()).isEqualTo(ParcelStatus.ENCUMBERED);

        parcel.clearEncumbrance(now);
        assertThat(parcel.status()).isEqualTo(ParcelStatus.ACTIVE);
    }

    @Test
    void attachesDocumentAndRetires() {
        LandParcel parcel = register();
        parcel.attachDocument(doc, now);
        parcel.attachDocument(doc, now);
        assertThat(parcel.documentIds()).containsExactly(doc);

        parcel.retire(now);
        assertThat(parcel.status()).isEqualTo(ParcelStatus.RETIRED);
        assertThatThrownBy(() -> parcel.attachDocument(
                        "doc_22222222-2222-4222-8222-222222222222", now))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void requiresLeaseReferenceForLeasehold() {
        assertThatThrownBy(() -> LandParcel.register(
                        TenantId.generate(),
                        ParcelNumber.allocate(2024, 1),
                        "Addis Ababa",
                        "Bole",
                        "Bole",
                        "03",
                        null,
                        LandUse.RESIDENTIAL,
                        TenureType.LEASEHOLD,
                        AreaSquareMeters.of("120.50"),
                        ParcelOwner.of(citizen, null),
                        null,
                        null,
                        null,
                        null,
                        now))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsOwnerlessParcel() {
        assertThatThrownBy(() -> ParcelOwner.of(null, null)).isInstanceOf(IllegalArgumentException.class);
    }

    private LandParcel register() {
        return LandParcel.register(
                TenantId.generate(),
                ParcelNumber.allocate(2024, 1),
                "Addis Ababa",
                "Bole",
                "Bole",
                "03",
                "Around Edna Mall",
                LandUse.RESIDENTIAL,
                TenureType.LEASEHOLD,
                AreaSquareMeters.of("250.00"),
                ParcelOwner.of(citizen, null),
                "TD-AA-2024-1",
                "LEASE-AA-9001",
                Instant.parse("2054-06-01T00:00:00Z"),
                null,
                now);
    }
}
