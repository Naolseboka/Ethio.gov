package et.gov.ethgov.gis.application.crs;

import et.gov.ethgov.gis.domain.crs.Crs;
import et.gov.ethgov.gis.domain.crs.CrsId;
import et.gov.ethgov.gis.domain.crs.CrsRepository;
import java.util.Objects;

public final class GetCrsUseCase {
    private final CrsRepository repository;

    public GetCrsUseCase(CrsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Crs execute(String rawId) {
        CrsId id = CrsId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("crs not found: " + rawId));
    }
}
