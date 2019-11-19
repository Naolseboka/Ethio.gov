package et.gov.ethgov.developer.application.quota;

import et.gov.ethgov.developer.domain.quota.Quota;
import et.gov.ethgov.developer.domain.quota.QuotaId;
import et.gov.ethgov.developer.domain.quota.QuotaRepository;
import java.util.Objects;

public final class GetQuotaUseCase {
    private final QuotaRepository repository;

    public GetQuotaUseCase(QuotaRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Quota execute(String rawId) {
        QuotaId id = QuotaId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("quota not found: " + rawId));
    }
}
