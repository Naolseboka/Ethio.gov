package et.gov.ethgov.directory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.directory.domain.employee.EmployeeNumber;
import et.gov.ethgov.directory.domain.employee.PublicEmployee;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeRegistered;
import et.gov.ethgov.directory.domain.employee.PublicEmployeeStatus;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnit;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitCreated;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitId;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitStatus;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitType;
import et.gov.ethgov.directory.domain.unit.UnitCode;
import et.gov.ethgov.directory.domain.unit.UnitName;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DirectoryDomainTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final LocalDate today = LocalDate.of(2024, 6, 1);
    private final TenantId tenantId = TenantId.generate();

    @Test
    void createsOrganizationalUnitAndEmitsEvent() {
        OrganizationalUnit unit = OrganizationalUnit.create(
                tenantId,
                UnitCode.of("FIN"),
                UnitName.of("Finance Bureau"),
                OrganizationalUnitType.BUREAU,
                null,
                null,
                "City finance",
                now);
        assertThat(unit.status()).isEqualTo(OrganizationalUnitStatus.ACTIVE);
        assertThat(unit.pullDomainEvents()).anyMatch(OrganizationalUnitCreated.class::isInstance);
    }

    @Test
    void rejectsSelfParentMove() {
        OrganizationalUnit unit = rootUnit();
        assertThatThrownBy(() -> unit.moveTo(unit.id(), now))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("own parent");
    }

    @Test
    void deactivatesAndBlocksEdits() {
        OrganizationalUnit unit = rootUnit();
        unit.pullDomainEvents();
        unit.deactivate(now);
        assertThat(unit.status()).isEqualTo(OrganizationalUnitStatus.INACTIVE);
        assertThatThrownBy(() -> unit.rename(UnitName.of("Other"), now))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Inactive");
        unit.reactivate(now);
        assertThat(unit.status()).isEqualTo(OrganizationalUnitStatus.ACTIVE);
    }

    @Test
    void registersEmployeeAndSeparates() {
        OrganizationalUnitId unitId = OrganizationalUnitId.generate();
        PublicEmployee employee = PublicEmployee.register(
                tenantId,
                EmployeeNumber.of("EMP-100"),
                "cit_11111111-1111-4111-8111-111111111111",
                "Abebe Bekele",
                unitId,
                "Budget Officer",
                LocalDate.of(2020, 1, 15),
                today,
                now);
        assertThat(employee.pullDomainEvents()).anyMatch(PublicEmployeeRegistered.class::isInstance);
        employee.separate(LocalDate.of(2024, 5, 1), today, now);
        assertThat(employee.status()).isEqualTo(PublicEmployeeStatus.SEPARATED);
        assertThatThrownBy(() -> employee.changeAssignment(unitId, "Other", now))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Separated");
    }

    @Test
    void linksIdentityOnce() {
        PublicEmployee employee = PublicEmployee.register(
                tenantId,
                EmployeeNumber.of("EMP-200"),
                "cit_22222222-2222-4222-8222-222222222222",
                "Sara Hailu",
                OrganizationalUnitId.generate(),
                "Clerk",
                LocalDate.of(2021, 3, 1),
                today,
                now);
        String account = "usr_11111111-1111-4111-8111-111111111111";
        employee.linkIdentityAccount(account, now);
        assertThat(employee.identityAccountId()).contains(account);
        assertThatThrownBy(
                        () -> employee.linkIdentityAccount("usr_22222222-2222-4222-8222-222222222222", now))
                .isInstanceOf(DomainException.class);
    }

    private OrganizationalUnit rootUnit() {
        return OrganizationalUnit.create(
                tenantId,
                UnitCode.of("ROOT"),
                UnitName.of("City Administration"),
                OrganizationalUnitType.OFFICE,
                null,
                null,
                null,
                now);
    }
}
