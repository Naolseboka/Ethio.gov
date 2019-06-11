package et.gov.ethgov.citizen.application.citizen;

import et.gov.ethgov.citizen.domain.citizen.Citizen;
import et.gov.ethgov.citizen.domain.citizen.CitizenId;
import et.gov.ethgov.citizen.domain.citizen.CitizenRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LinkIdentityAccountUseCase {

    private final CitizenRepository citizens;
    private final CitizenEventPublisher eventPublisher;
    private final Clock clock;

    public LinkIdentityAccountUseCase(
            CitizenRepository citizens, CitizenEventPublisher eventPublisher, Clock clock) {
        this.citizens = citizens;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String citizenId, String identityAccountId) {
        Objects.requireNonNull(citizenId, "citizenId");
        Citizen citizen = citizens
                .findById(CitizenId.of(citizenId))
                .orElseThrow(() -> new DomainException(
                        "CITIZEN_NOT_FOUND", "Citizen Not Found", "Citizen does not exist", 404));
        citizen.linkIdentityAccount(identityAccountId, clock.instant());
        citizens.save(citizen);
        eventPublisher.publishAll(citizen.pullDomainEvents());
    }
}
