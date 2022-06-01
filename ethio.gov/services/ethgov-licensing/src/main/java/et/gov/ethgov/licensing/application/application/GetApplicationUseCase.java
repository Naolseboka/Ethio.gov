package et.gov.ethgov.licensing.application.application;

import et.gov.ethgov.licensing.domain.application.Application;
import et.gov.ethgov.licensing.domain.application.ApplicationId;
import et.gov.ethgov.licensing.domain.application.ApplicationRepository;
import java.util.Objects;

public final class GetApplicationUseCase {
    private final ApplicationRepository repository;

    public GetApplicationUseCase(ApplicationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Application execute(String rawId) {
        ApplicationId id = ApplicationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("application not found: " + rawId));
    }
}
