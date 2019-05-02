package et.gov.ethgov.casemgmt.application.casefile;

import et.gov.ethgov.casemgmt.application.CaseEventPublisher;
import et.gov.ethgov.casemgmt.domain.casefile.CaseId;
import et.gov.ethgov.casemgmt.domain.casefile.CasePriority;
import et.gov.ethgov.casemgmt.domain.casefile.CaseRepository;
import et.gov.ethgov.casemgmt.domain.casefile.GovernmentCase;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCaseUseCase {

    private final CaseRepository cases;
    private final CaseEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateCaseUseCase(CaseRepository cases, CaseEventPublisher eventPublisher, Clock clock) {
        this.cases = cases;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        GovernmentCase governmentCase = require(command.caseId());
        governmentCase.updateDetails(
                command.title(),
                command.description(),
                CasePriority.valueOf(command.priority()),
                clock.instant());
        cases.save(governmentCase);
        eventPublisher.publishAll(governmentCase.pullDomainEvents());
    }

    private GovernmentCase require(String caseId) {
        return cases.findById(CaseId.of(caseId))
                .orElseThrow(() -> new DomainException(
                        "CASE_NOT_FOUND", "Case Not Found", "Case does not exist", 404));
    }

    public record Command(String caseId, String title, String description, String priority) {}
}
