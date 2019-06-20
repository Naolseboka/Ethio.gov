package et.gov.ethgov.citizen.application.disabilitystatus;

import et.gov.ethgov.citizen.domain.disabilitystatus.Disabilitystatus;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusId;
import et.gov.ethgov.citizen.domain.disabilitystatus.DisabilitystatusRepository;
import java.util.Objects;

public final class GetDisabilitystatusUseCase {
    private final DisabilitystatusRepository repository;

    public GetDisabilitystatusUseCase(DisabilitystatusRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Disabilitystatus execute(String rawId) {
        DisabilitystatusId id = DisabilitystatusId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("disabilitystatus not found: " + rawId));
    }
}
