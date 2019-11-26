package et.gov.ethgov.developer.application.usage;

import et.gov.ethgov.developer.domain.usage.Usage;
import et.gov.ethgov.developer.domain.usage.UsageId;
import et.gov.ethgov.developer.domain.usage.UsageRepository;
import java.util.Objects;

public final class GetUsageUseCase {
    private final UsageRepository repository;

    public GetUsageUseCase(UsageRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Usage execute(String rawId) {
        UsageId id = UsageId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("usage not found: " + rawId));
    }
}
