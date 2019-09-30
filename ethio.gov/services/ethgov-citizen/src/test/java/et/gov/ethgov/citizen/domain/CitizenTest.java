package et.gov.ethgov.citizen.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.citizen.domain.citizen.Citizen;
import et.gov.ethgov.citizen.domain.citizen.CitizenDeceased;
import et.gov.ethgov.citizen.domain.citizen.CitizenStatus;
import et.gov.ethgov.citizen.domain.citizen.FaydaId;
import et.gov.ethgov.citizen.domain.citizen.PersonName;
import et.gov.ethgov.citizen.domain.citizen.Sex;
import et.gov.ethgov.platform.commons.address.EthiopianAddress;
import et.gov.ethgov.platform.commons.address.RegionalState;
import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CitizenTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final LocalDate today = LocalDate.of(2024, 6, 1);

    @Test
    void registersAndExposesEthiopianDob() {
        Citizen citizen = registerSample();
        assertThat(citizen.status()).isEqualTo(CitizenStatus.ACTIVE);
        assertThat(citizen.dateOfBirthEthiopian().toString()).contains("E.C.");
        assertThat(citizen.pullDomainEvents()).hasSize(1);
    }

    @Test
    void rejectsFutureDateOfBirth() {
        assertThatThrownBy(() -> Citizen.register(
                        PersonName.of("Abebe", "Bekele", "Tesfaye"),
                        today.plusDays(1),
                        Sex.MALE,
                        address(),
                        EthiopianPhoneNumber.parse("0911223344"),
                        null,
                        TenantId.generate(),
                        now,
                        today))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void recordsDeathAndBlocksFurtherEdits() {
        Citizen citizen = registerSample();
        citizen.pullDomainEvents();
        citizen.recordDeath(LocalDate.of(2023, 1, 1), now, today);
        assertThat(citizen.status()).isEqualTo(CitizenStatus.DECEASED);
        assertThat(citizen.pullDomainEvents()).anyMatch(CitizenDeceased.class::isInstance);
        assertThatThrownBy(() -> citizen.assignFaydaId(FaydaId.of("123456789012"), now))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Deceased");
    }

    @Test
    void linksIdentityOnce() {
        Citizen citizen = registerSample();
        String accountId = "usr_11111111-1111-4111-8111-111111111111";
        citizen.linkIdentityAccount(accountId, now);
        assertThat(citizen.identityAccountId()).contains(accountId);
        assertThatThrownBy(() -> citizen.linkIdentityAccount("usr_22222222-2222-4222-8222-222222222222", now))
                .isInstanceOf(DomainException.class);
    }

    private Citizen registerSample() {
        return Citizen.register(
                PersonName.of("Abebe", "Bekele", "Tesfaye"),
                LocalDate.of(1990, 5, 10),
                Sex.MALE,
                address(),
                EthiopianPhoneNumber.parse("0911223344"),
                null,
                TenantId.generate(),
                now,
                today);
    }

    private static EthiopianAddress address() {
        return EthiopianAddress.builder()
                .regionalState(RegionalState.ADDIS_ABABA)
                .zone("AA-BOLE", "Bole")
                .woreda("AA-BOLE-03", "Woreda 03")
                .kebele("AA-BOLE-03-05", "Kebele 05")
                .street("Africa Avenue")
                .houseNumber("12")
                .build();
    }
}
