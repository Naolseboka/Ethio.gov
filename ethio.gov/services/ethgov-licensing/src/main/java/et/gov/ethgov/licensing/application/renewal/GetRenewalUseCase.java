package et.gov.ethgov.licensing.application.renewal;

import et.gov.ethgov.licensing.domain.renewal.Renewal;
import et.gov.ethgov.licensing.domain.renewal.RenewalId;
import et.gov.ethgov.licensing.domain.renewal.RenewalRepository;
import java.util.Objects;

public final class GetRenewalUseCase {
    private final RenewalRepository repository;

    public GetRenewalUseCase(RenewalRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Renewal execute(String rawId) {
        RenewalId id = RenewalId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("renewal not found: " + rawId));
    }
}
