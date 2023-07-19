package et.gov.ethgov.organization.application.mandate;

import et.gov.ethgov.organization.domain.mandate.Mandate;
import et.gov.ethgov.organization.domain.mandate.MandateId;
import et.gov.ethgov.organization.domain.mandate.MandateRepository;
import java.util.Objects;

public final class GetMandateUseCase {
    private final MandateRepository repository;

    public GetMandateUseCase(MandateRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Mandate execute(String rawId) {
        MandateId id = MandateId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mandate not found: " + rawId));
    }
}
