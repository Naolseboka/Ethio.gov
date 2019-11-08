package et.gov.ethgov.developer.application.devwebhook;

import et.gov.ethgov.developer.domain.devwebhook.Devwebhook;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookId;
import et.gov.ethgov.developer.domain.devwebhook.DevwebhookRepository;
import java.util.Objects;

public final class GetDevwebhookUseCase {
    private final DevwebhookRepository repository;

    public GetDevwebhookUseCase(DevwebhookRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Devwebhook execute(String rawId) {
        DevwebhookId id = DevwebhookId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devwebhook not found: " + rawId));
    }
}
