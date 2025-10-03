package et.gov.ethgov.workflow.application.checklist;

import et.gov.ethgov.workflow.domain.checklist.Checklist;
import et.gov.ethgov.workflow.domain.checklist.ChecklistId;
import et.gov.ethgov.workflow.domain.checklist.ChecklistRepository;
import java.util.Objects;

public final class GetChecklistUseCase {
    private final ChecklistRepository repository;

    public GetChecklistUseCase(ChecklistRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Checklist execute(String rawId) {
        ChecklistId id = ChecklistId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("checklist not found: " + rawId));
    }
}
