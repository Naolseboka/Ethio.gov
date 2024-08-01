package et.gov.ethgov.permits.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.permits.domain.permit.ConstructionPermit;
import et.gov.ethgov.permits.domain.permit.FloorAreaSquareMeters;
import et.gov.ethgov.permits.domain.permit.PermitApplicant;
import et.gov.ethgov.permits.domain.permit.PermitApplied;
import et.gov.ethgov.permits.domain.permit.PermitNumber;
import et.gov.ethgov.permits.domain.permit.PermitStatus;
import et.gov.ethgov.permits.domain.permit.PermitType;
import et.gov.ethgov.permits.domain.permit.StructureUse;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.money.Money;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ConstructionPermitTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final String org = "org_11111111-1111-4111-8111-111111111111";
    private final String parcel = "par_11111111-1111-4111-8111-111111111111";
    private final String doc = "doc_11111111-1111-4111-8111-111111111111";

    @Test
    void appliesReviewsIssuesActivatesAndCompletes() {
        ConstructionPermit permit = apply();
        assertThat(permit.status()).isEqualTo(PermitStatus.APPLIED);
        assertThat(permit.pullDomainEvents()).anyMatch(PermitApplied.class::isInstance);

        permit.startReview(now);
        permit.issue(
                Instant.parse("2024-07-01T00:00:00Z"), Instant.parse("2026-07-01T00:00:00Z"), now);
        permit.activate(now);
        assertThat(permit.status()).isEqualTo(PermitStatus.ACTIVE);

        permit.suspend("Unsafe scaffolding", now);
        permit.reinstate(now);
        permit.complete(now);
        assertThat(permit.status()).isEqualTo(PermitStatus.COMPLETED);
    }

    @Test
    void requiresParcelAndRejectsTerminalMutation() {
        assertThatThrownBy(() -> ConstructionPermit.apply(
                        TenantId.generate(),
                        PermitNumber.allocate(2024, 1),
                        PermitType.NEW_BUILDING,
                        StructureUse.RESIDENTIAL,
                        "House",
                        null,
                        PermitApplicant.of(null, org),
                        "bad",
                        2,
                        FloorAreaSquareMeters.of("120.00"),
                        null,
                        "Addis Ababa",
                        null,
                        "Bole",
                        null,
                        null,
                        null,
                        now))
                .isInstanceOf(IllegalArgumentException.class);

        ConstructionPermit permit = apply();
        permit.attachDocument(doc, now);
        permit.reject("Incomplete structural drawings", now);
        assertThatThrownBy(() -> permit.attachDocument(
                        "doc_22222222-2222-4222-8222-222222222222", now))
                .isInstanceOf(DomainException.class);
    }

    private ConstructionPermit apply() {
        return ConstructionPermit.apply(
                TenantId.generate(),
                PermitNumber.allocate(2024, 1),
                PermitType.NEW_BUILDING,
                StructureUse.RESIDENTIAL,
                "Bole G+2 Residence",
                "Two-storey residential building",
                PermitApplicant.of(null, org),
                parcel,
                3,
                FloorAreaSquareMeters.of("350.00"),
                Money.etb("2500000.00"),
                "Addis Ababa",
                "Bole",
                "Bole",
                "03",
                "Around Edna Mall",
                null,
                now);
    }
}
