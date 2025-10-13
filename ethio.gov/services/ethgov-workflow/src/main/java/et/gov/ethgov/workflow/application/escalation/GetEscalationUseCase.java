package et.gov.ethgov.workflow.application.escalation;

import et.gov.ethgov.workflow.domain.escalation.Escalation;
import et.gov.ethgov.workflow.domain.escalation.EscalationId;
import et.gov.ethgov.workflow.domain.escalation.EscalationRepository;
import java.util.Objects;

public final class GetEscalationUseCase {
    private final EscalationRepository repository;

    public GetEscalationUseCase(EscalationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Escalation execute(String rawId) {
        EscalationId id = EscalationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("escalation not found: " + rawId));
    }
}
