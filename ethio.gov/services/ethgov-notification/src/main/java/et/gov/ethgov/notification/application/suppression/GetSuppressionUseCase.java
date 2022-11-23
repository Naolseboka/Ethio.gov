package et.gov.ethgov.notification.application.suppression;

import et.gov.ethgov.notification.domain.suppression.Suppression;
import et.gov.ethgov.notification.domain.suppression.SuppressionId;
import et.gov.ethgov.notification.domain.suppression.SuppressionRepository;
import java.util.Objects;

public final class GetSuppressionUseCase {
    private final SuppressionRepository repository;

    public GetSuppressionUseCase(SuppressionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Suppression execute(String rawId) {
        SuppressionId id = SuppressionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("suppression not found: " + rawId));
    }
}
