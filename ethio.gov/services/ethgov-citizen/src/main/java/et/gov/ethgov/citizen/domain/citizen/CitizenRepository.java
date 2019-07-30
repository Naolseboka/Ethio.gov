package et.gov.ethgov.citizen.domain.citizen;

import et.gov.ethgov.platform.commons.contact.EthiopianPhoneNumber;
import java.util.Optional;

public interface CitizenRepository {

    boolean existsByPhone(EthiopianPhoneNumber phone);

    boolean existsByFaydaId(FaydaId faydaId);

    Optional<Citizen> findById(CitizenId id);

    Optional<Citizen> findByPhone(EthiopianPhoneNumber phone);

    Optional<Citizen> findByFaydaId(FaydaId faydaId);

    void save(Citizen citizen);
}
