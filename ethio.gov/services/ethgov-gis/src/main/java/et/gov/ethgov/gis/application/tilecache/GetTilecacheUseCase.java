package et.gov.ethgov.gis.application.tilecache;

import et.gov.ethgov.gis.domain.tilecache.Tilecache;
import et.gov.ethgov.gis.domain.tilecache.TilecacheId;
import et.gov.ethgov.gis.domain.tilecache.TilecacheRepository;
import java.util.Objects;

public final class GetTilecacheUseCase {
    private final TilecacheRepository repository;

    public GetTilecacheUseCase(TilecacheRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Tilecache execute(String rawId) {
        TilecacheId id = TilecacheId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("tilecache not found: " + rawId));
    }
}
