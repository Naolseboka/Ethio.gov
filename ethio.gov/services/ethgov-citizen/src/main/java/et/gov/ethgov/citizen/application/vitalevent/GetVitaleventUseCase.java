package et.gov.ethgov.citizen.application.vitalevent;

import et.gov.ethgov.citizen.domain.vitalevent.Vitalevent;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventId;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventRepository;
import java.util.Objects;

public final class GetVitaleventUseCase {
    private final VitaleventRepository repository;

    public GetVitaleventUseCase(VitaleventRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Vitalevent execute(String rawId) {
        VitaleventId id = VitaleventId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("vitalevent not found: " + rawId));
    }
}
