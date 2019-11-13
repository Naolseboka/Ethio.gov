package et.gov.ethgov.developer.application.portaluser;

import et.gov.ethgov.developer.domain.portaluser.Portaluser;
import et.gov.ethgov.developer.domain.portaluser.PortaluserId;
import et.gov.ethgov.developer.domain.portaluser.PortaluserRepository;
import java.util.Objects;

public final class GetPortaluserUseCase {
    private final PortaluserRepository repository;

    public GetPortaluserUseCase(PortaluserRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Portaluser execute(String rawId) {
        PortaluserId id = PortaluserId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("portaluser not found: " + rawId));
    }
}
