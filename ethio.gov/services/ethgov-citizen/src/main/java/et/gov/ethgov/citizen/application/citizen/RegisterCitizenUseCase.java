package et.gov.ethgov.citizen.application.citizen;

import et.gov.ethgov.citizen.domain.citizen.Citizen;
import et.gov.ethgov.citizen.domain.citizen.CitizenId;
import et.gov.ethgov.citizen.domain.citizen.CitizenRepository;
import et.gov.ethgov.citizen.domain.citizen.FaydaId;
import et.gov.ethgov.citizen.domain.citizen.PersonName;
import et.gov.ethgov.citizen.domain.citizen.Sex;
import et.gov.ethgov.platform.commons.address.EthiopianAddress;
import et.gov.ethgov.platform.commons.address.RegionalState;
import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterCitizenUseCase {

    private final CitizenRepository citizens;
    private final CitizenEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterCitizenUseCase(
            CitizenRepository citizens, CitizenEventPublisher eventPublisher, Clock clock) {
        this.citizens = citizens;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public CitizenId execute(Command command) {
        Objects.requireNonNull(command, "command");
        EthiopianPhoneNumber phone = EthiopianPhoneNumber.parse(command.phone());
        if (citizens.existsByPhone(phone)) {
            throw new DomainException(
                    "PHONE_ALREADY_REGISTERED",
                    "Phone Already Registered",
                    "A citizen with this phone number already exists",
                    409);
        }
        FaydaId faydaId = null;
        if (command.faydaId() != null && !command.faydaId().isBlank()) {
            faydaId = FaydaId.of(command.faydaId());
            if (citizens.existsByFaydaId(faydaId)) {
                throw new DomainException(
                        "FAYDA_ALREADY_REGISTERED",
                        "Fayda Already Registered",
                        "A citizen with this Fayda ID already exists",
                        409);
            }
        }
        EthiopianAddress address = EthiopianAddress.builder()
                .regionalState(RegionalState.fromCode(command.regionCode()))
                .zone(command.zoneCode(), command.zoneName())
                .woreda(command.woredaCode(), command.woredaName())
                .kebele(command.kebeleCode(), command.kebeleName())
                .locality(command.locality())
                .street(command.street())
                .houseNumber(command.houseNumber())
                .build();
        LocalDate today = LocalDate.now(EthGovClocks.ADDIS_ABABA);
        Citizen citizen = Citizen.register(
                PersonName.of(command.givenName(), command.fatherName(), command.grandfatherName()),
                LocalDate.parse(command.dateOfBirth()),
                Sex.valueOf(command.sex()),
                address,
                phone,
                faydaId,
                TenantId.of(command.registeringTenantId()),
                clock.instant(),
                today);
        citizens.save(citizen);
        eventPublisher.publishAll(citizen.pullDomainEvents());
        return citizen.id();
    }

    public record Command(
            String givenName,
            String fatherName,
            String grandfatherName,
            String dateOfBirth,
            String sex,
            String phone,
            String faydaId,
            String registeringTenantId,
            String regionCode,
            String zoneCode,
            String zoneName,
            String woredaCode,
            String woredaName,
            String kebeleCode,
            String kebeleName,
            String locality,
            String street,
            String houseNumber) {}
}
