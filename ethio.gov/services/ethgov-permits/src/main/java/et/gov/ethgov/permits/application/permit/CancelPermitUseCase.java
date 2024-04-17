package et.gov.ethgov.permits.application.permit;

import et.gov.ethgov.permits.application.PermitsEventPublisher;
import et.gov.ethgov.permits.domain.permit.ConstructionPermit;
import et.gov.ethgov.permits.domain.permit.PermitId;
import et.gov.ethgov.permits.domain.permit.PermitRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelPermitUseCase {

    private final PermitRepository permits;
    private final PermitsEventPublisher eventPublisher;
    private final Clock clock;

    public CancelPermitUseCase(PermitRepository permits, PermitsEventPublisher eventPublisher, Clock clock) {
        this.permits = permits;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String permitId, String reason) {
        Objects.requireNonNull(permitId, "permitId");
        ConstructionPermit permit = permits.findById(PermitId.of(permitId))
                .orElseThrow(() -> new DomainException(
                        "PERMIT_NOT_FOUND", "Permit Not Found", "Permit does not exist", 404));
        permit.cancel(reason, clock.instant());
        permits.save(permit);
        eventPublisher.publishAll(permit.pullDomainEvents());
    }
}
