package et.gov.ethgov.gis.application.layer;

import et.gov.ethgov.gis.domain.layer.Layer;
import et.gov.ethgov.gis.domain.layer.LayerId;
import et.gov.ethgov.gis.domain.layer.LayerRepository;
import java.util.Objects;

public final class GetLayerUseCase {
    private final LayerRepository repository;

    public GetLayerUseCase(LayerRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Layer execute(String rawId) {
        LayerId id = LayerId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("layer not found: " + rawId));
    }
}
