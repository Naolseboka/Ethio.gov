package et.gov.ethgov.casemgmt.application.casefile;

import et.gov.ethgov.casemgmt.application.CaseEventPublisher;
import et.gov.ethgov.casemgmt.domain.casefile.CaseId;
import et.gov.ethgov.casemgmt.domain.casefile.CaseNumber;
import et.gov.ethgov.casemgmt.domain.casefile.CaseNumberSequence;
import et.gov.ethgov.casemgmt.domain.casefile.CaseParty;
import et.gov.ethgov.casemgmt.domain.casefile.CasePriority;
import et.gov.ethgov.casemgmt.domain.casefile.CaseRepository;
import et.gov.ethgov.casemgmt.domain.casefile.CaseType;
import et.gov.ethgov.casemgmt.domain.casefile.GovernmentCase;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpenCaseUseCase {

    private final CaseRepository cases;
    private final CaseNumberSequence sequences;
    private final CaseEventPublisher eventPublisher;
    private final Clock clock;

    public OpenCaseUseCase(
            CaseRepository cases,
            CaseNumberSequence sequences,
            CaseEventPublisher eventPublisher,
            Clock clock) {
        this.cases = cases;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public CaseId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        CaseNumber caseNumber = CaseNumber.allocate(year, sequences.nextSequence(tenantId, year));
        GovernmentCase governmentCase = GovernmentCase.open(
                tenantId,
                caseNumber,
                CaseType.valueOf(command.type()),
                command.title(),
                command.description(),
                CasePriority.valueOf(command.priority()),
                CaseParty.of(command.citizenId(), command.organizationId()),
                command.owningUnitId(),
                clock.instant());
        cases.save(governmentCase);
        eventPublisher.publishAll(governmentCase.pullDomainEvents());
        return governmentCase.id();
    }

    public record Command(
            String tenantId,
            String type,
            String title,
            String description,
            String priority,
            String citizenId,
            String organizationId,
            String owningUnitId) {}
}
