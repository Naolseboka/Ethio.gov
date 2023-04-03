package et.gov.ethgov.opendata.application.resource;

import et.gov.ethgov.opendata.domain.resource.Resource;
import et.gov.ethgov.opendata.domain.resource.ResourceId;
import et.gov.ethgov.opendata.domain.resource.ResourceRepository;
import java.util.Objects;

public final class GetResourceUseCase {
    private final ResourceRepository repository;

    public GetResourceUseCase(ResourceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Resource execute(String rawId) {
        ResourceId id = ResourceId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("resource not found: " + rawId));
    }
}
