package et.gov.ethgov.developer.application.devapplication;

import et.gov.ethgov.developer.domain.devapplication.Devapplication;
import et.gov.ethgov.developer.domain.devapplication.DevapplicationId;
import et.gov.ethgov.developer.domain.devapplication.DevapplicationRepository;
import java.util.Objects;

public final class GetDevapplicationUseCase {
    private final DevapplicationRepository repository;

    public GetDevapplicationUseCase(DevapplicationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Devapplication execute(String rawId) {
        DevapplicationId id = DevapplicationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devapplication not found: " + rawId));
    }
}
