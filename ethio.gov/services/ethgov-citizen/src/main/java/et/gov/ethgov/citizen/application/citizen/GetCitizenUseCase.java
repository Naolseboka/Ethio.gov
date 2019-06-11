package et.gov.ethgov.citizen.application.citizen;

import et.gov.ethgov.citizen.domain.citizen.Citizen;
import et.gov.ethgov.citizen.domain.citizen.CitizenId;
import et.gov.ethgov.citizen.domain.citizen.CitizenRepository;
import et.gov.ethgov.citizen.domain.citizen.FaydaId;
import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCitizenUseCase {

    private final CitizenRepository citizens;

    public GetCitizenUseCase(CitizenRepository citizens) {
        this.citizens = citizens;
    }

    @Transactional(readOnly = true)
    public Citizen byId(String citizenId) {
        return citizens
                .findById(CitizenId.of(citizenId))
                .orElseThrow(() -> new DomainException(
                        "CITIZEN_NOT_FOUND", "Citizen Not Found", "Citizen does not exist", 404));
    }

    @Transactional(readOnly = true)
    public Optional<Citizen> byPhone(String phone) {
        return citizens.findByPhone(EthiopianPhoneNumber.parse(phone));
    }

    @Transactional(readOnly = true)
    public Optional<Citizen> byFaydaId(String faydaId) {
        return citizens.findByFaydaId(FaydaId.of(faydaId));
    }
}
