package et.gov.ethgov.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.organization.domain.organization.LegalName;
import et.gov.ethgov.organization.domain.organization.Organization;
import et.gov.ethgov.organization.domain.organization.OrganizationRegistered;
import et.gov.ethgov.organization.domain.organization.OrganizationStatus;
import et.gov.ethgov.organization.domain.organization.OrganizationSuspended;
import et.gov.ethgov.organization.domain.organization.OrganizationType;
import et.gov.ethgov.organization.domain.organization.TinNumber;
import et.gov.ethgov.platform.commons.address.EthiopianAddress;
import et.gov.ethgov.platform.commons.address.RegionalState;
import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class OrganizationTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final LocalDate today = LocalDate.of(2024, 6, 1);

    @Test
    void registersPrivateCompanyAndEmitsEvent() {
        Organization organization = registerPrivateCompany();
        assertThat(organization.status()).isEqualTo(OrganizationStatus.ACTIVE);
        assertThat(organization.type()).isEqualTo(OrganizationType.PRIVATE_COMPANY);
        assertThat(organization.pullDomainEvents()).anyMatch(OrganizationRegistered.class::isInstance);
    }

    @Test
    void soleProprietorshipRequiresOwnerCitizenId() {
        assertThatThrownBy(() -> Organization.register(
                        LegalName.of("Abebe Trading"),
                        null,
                        OrganizationType.SOLE_PROPRIETORSHIP,
                        address(),
                        EthiopianPhoneNumber.parse("0911223344"),
                        null,
                        TenantId.generate(),
                        null,
                        LocalDate.of(2010, 1, 1),
                        today,
                        now))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void assignsTinOnceAndRejectsChange() {
        Organization organization = registerPrivateCompany();
        organization.pullDomainEvents();
        TinNumber tin = TinNumber.of("0012345678");
        organization.assignTin(tin, now);
        assertThat(organization.tin()).contains(tin);
        organization.assignTin(tin, now);
        assertThat(organization.pullDomainEvents()).hasSize(1);
        assertThatThrownBy(() -> organization.assignTin(TinNumber.of("0098765432"), now))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("already has a TIN");
    }

    @Test
    void suspendReinstateAndDissolveLifecycle() {
        Organization organization = registerPrivateCompany();
        organization.pullDomainEvents();
        organization.suspend("Regulatory non-compliance", now);
        assertThat(organization.status()).isEqualTo(OrganizationStatus.SUSPENDED);
        assertThat(organization.pullDomainEvents()).anyMatch(OrganizationSuspended.class::isInstance);

        assertThatThrownBy(() -> organization.assignTin(TinNumber.of("0012345678"), now))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Suspended");

        organization.reinstate(now);
        assertThat(organization.status()).isEqualTo(OrganizationStatus.ACTIVE);

        organization.dissolve(now);
        assertThat(organization.status()).isEqualTo(OrganizationStatus.DISSOLVED);
        assertThatThrownBy(() -> organization.suspend("too late now", now)).isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsInvalidTin() {
        assertThatThrownBy(() -> TinNumber.of("123")).isInstanceOf(IllegalArgumentException.class);
    }

    private Organization registerPrivateCompany() {
        return Organization.register(
                LegalName.of("Ethio Logistics PLC"),
                "Ethio Logistics",
                OrganizationType.PRIVATE_COMPANY,
                address(),
                EthiopianPhoneNumber.parse("0911223344"),
                "info@ethiologistics.et",
                TenantId.generate(),
                null,
                LocalDate.of(2015, 3, 15),
                today,
                now);
    }

    private static EthiopianAddress address() {
        return EthiopianAddress.builder()
                .regionalState(RegionalState.ADDIS_ABABA)
                .zone("AA-BOLE", "Bole")
                .woreda("AA-BOLE-03", "Woreda 03")
                .kebele("AA-BOLE-03-05", "Kebele 05")
                .street("Africa Avenue")
                .houseNumber("100")
                .build();
    }
}
