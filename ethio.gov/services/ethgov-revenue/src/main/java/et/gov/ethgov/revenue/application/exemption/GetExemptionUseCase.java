package et.gov.ethgov.revenue.application.exemption;

import et.gov.ethgov.revenue.domain.exemption.Exemption;
import et.gov.ethgov.revenue.domain.exemption.ExemptionId;
import et.gov.ethgov.revenue.domain.exemption.ExemptionRepository;
import java.util.Objects;

public final class GetExemptionUseCase {
    private final ExemptionRepository repository;

    public GetExemptionUseCase(ExemptionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Exemption execute(String rawId) {
        ExemptionId id = ExemptionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("exemption not found: " + rawId));
    }
}
