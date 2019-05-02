package et.gov.ethgov.casemgmt.application.casefile;

import et.gov.ethgov.casemgmt.application.CaseEventPublisher;
import et.gov.ethgov.casemgmt.domain.casefile.CaseId;
import et.gov.ethgov.casemgmt.domain.casefile.CaseRepository;
import et.gov.ethgov.casemgmt.domain.casefile.GovernmentCase;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LinkCaseWorkflowUseCase {

    private final CaseRepository cases;
    private final CaseEventPublisher eventPublisher;
    private final Clock clock;

    public LinkCaseWorkflowUseCase(CaseRepository cases, CaseEventPublisher eventPublisher, Clock clock) {
        this.cases = cases;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String caseId, String processInstanceId) {
        Objects.requireNonNull(caseId, "caseId");
        GovernmentCase governmentCase = cases.findById(CaseId.of(caseId))
                .orElseThrow(() -> new DomainException(
                        "CASE_NOT_FOUND", "Case Not Found", "Case does not exist", 404));
        governmentCase.linkWorkflow(processInstanceId, clock.instant());
        cases.save(governmentCase);
        eventPublisher.publishAll(governmentCase.pullDomainEvents());
    }
}
