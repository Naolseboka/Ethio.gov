package et.gov.ethgov.developer.application.sandbox;

import et.gov.ethgov.developer.domain.sandbox.Sandbox;
import et.gov.ethgov.developer.domain.sandbox.SandboxId;
import et.gov.ethgov.developer.domain.sandbox.SandboxRepository;
import java.util.Objects;

public final class GetSandboxUseCase {
    private final SandboxRepository repository;

    public GetSandboxUseCase(SandboxRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Sandbox execute(String rawId) {
        SandboxId id = SandboxId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("sandbox not found: " + rawId));
    }
}
