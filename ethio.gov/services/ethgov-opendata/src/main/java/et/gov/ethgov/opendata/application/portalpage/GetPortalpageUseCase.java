package et.gov.ethgov.opendata.application.portalpage;

import et.gov.ethgov.opendata.domain.portalpage.Portalpage;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageId;
import et.gov.ethgov.opendata.domain.portalpage.PortalpageRepository;
import java.util.Objects;

public final class GetPortalpageUseCase {
    private final PortalpageRepository repository;

    public GetPortalpageUseCase(PortalpageRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Portalpage execute(String rawId) {
        PortalpageId id = PortalpageId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("portalpage not found: " + rawId));
    }
}
