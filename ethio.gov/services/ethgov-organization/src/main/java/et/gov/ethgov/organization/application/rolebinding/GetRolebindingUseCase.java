package et.gov.ethgov.organization.application.rolebinding;

import et.gov.ethgov.organization.domain.rolebinding.Rolebinding;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingId;
import et.gov.ethgov.organization.domain.rolebinding.RolebindingRepository;
import java.util.Objects;

public final class GetRolebindingUseCase {
    private final RolebindingRepository repository;

    public GetRolebindingUseCase(RolebindingRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Rolebinding execute(String rawId) {
        RolebindingId id = RolebindingId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("rolebinding not found: " + rawId));
    }
}
