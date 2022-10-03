package et.gov.ethgov.licensing.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.licensing.domain.license.BusinessLicense;
import et.gov.ethgov.licensing.domain.license.LicenseApplied;
import et.gov.ethgov.licensing.domain.license.LicenseHolder;
import et.gov.ethgov.licensing.domain.license.LicenseNumber;
import et.gov.ethgov.licensing.domain.license.LicenseStatus;
import et.gov.ethgov.licensing.domain.license.LicenseType;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class BusinessLicenseTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final String org = "org_11111111-1111-4111-8111-111111111111";
    private final String doc = "doc_11111111-1111-4111-8111-111111111111";

    @Test
    void appliesReviewsIssuesActivatesAndSuspends() {
        BusinessLicense license = apply();
        assertThat(license.status()).isEqualTo(LicenseStatus.APPLIED);
        assertThat(license.pullDomainEvents()).anyMatch(LicenseApplied.class::isInstance);

        license.startReview(now);
        license.issue(
                Instant.parse("2024-07-01T00:00:00Z"), Instant.parse("2025-07-01T00:00:00Z"), now);
        license.activate(now);
        assertThat(license.status()).isEqualTo(LicenseStatus.ACTIVE);

        license.suspend("Health inspection failed", now);
        assertThat(license.status()).isEqualTo(LicenseStatus.SUSPENDED);
        license.reinstate(now);
        assertThat(license.status()).isEqualTo(LicenseStatus.ACTIVE);
    }

    @Test
    void attachesDocumentAndRejectsTerminalMutation() {
        BusinessLicense license = apply();
        license.attachDocument(doc, now);
        license.reject("Incomplete premises documents", now);
        assertThat(license.status()).isEqualTo(LicenseStatus.REJECTED);
        assertThatThrownBy(() -> license.attachDocument(
                        "doc_22222222-2222-4222-8222-222222222222", now))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void requiresOrganizationHolder() {
        assertThatThrownBy(() -> LicenseHolder.of(null, "cit_11111111-1111-4111-8111-111111111111"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private BusinessLicense apply() {
        return BusinessLicense.apply(
                TenantId.generate(),
                LicenseNumber.allocate(2024, 1),
                LicenseType.TRADE,
                "Bole Retail Shop",
                "General merchandise retail",
                LicenseHolder.of(org, null),
                "Addis Ababa",
                "Bole",
                "Bole",
                "03",
                "Around Edna Mall",
                null,
                null,
                now);
    }
}
