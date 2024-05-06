package et.gov.ethgov.permits.application.permitinspection;

import et.gov.ethgov.permits.domain.permitinspection.Permitinspection;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionId;
import et.gov.ethgov.permits.domain.permitinspection.PermitinspectionRepository;
import java.util.Objects;

public final class GetPermitinspectionUseCase {
    private final PermitinspectionRepository repository;

    public GetPermitinspectionUseCase(PermitinspectionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Permitinspection execute(String rawId) {
        PermitinspectionId id = PermitinspectionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitinspection not found: " + rawId));
    }
}
