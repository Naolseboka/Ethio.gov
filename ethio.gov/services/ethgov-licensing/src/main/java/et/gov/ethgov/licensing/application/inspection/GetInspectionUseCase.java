package et.gov.ethgov.licensing.application.inspection;

import et.gov.ethgov.licensing.domain.inspection.Inspection;
import et.gov.ethgov.licensing.domain.inspection.InspectionId;
import et.gov.ethgov.licensing.domain.inspection.InspectionRepository;
import java.util.Objects;

public final class GetInspectionUseCase {
    private final InspectionRepository repository;

    public GetInspectionUseCase(InspectionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Inspection execute(String rawId) {
        InspectionId id = InspectionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("inspection not found: " + rawId));
    }
}
