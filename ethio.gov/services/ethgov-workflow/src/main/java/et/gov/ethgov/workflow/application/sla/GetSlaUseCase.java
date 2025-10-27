package et.gov.ethgov.workflow.application.sla;

import et.gov.ethgov.workflow.domain.sla.Sla;
import et.gov.ethgov.workflow.domain.sla.SlaId;
import et.gov.ethgov.workflow.domain.sla.SlaRepository;
import java.util.Objects;

public final class GetSlaUseCase {
    private final SlaRepository repository;

    public GetSlaUseCase(SlaRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Sla execute(String rawId) {
        SlaId id = SlaId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("sla not found: " + rawId));
    }
}
