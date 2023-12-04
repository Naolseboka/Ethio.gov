package et.gov.ethgov.payments.application.cutoff;

import et.gov.ethgov.payments.domain.cutoff.Cutoff;
import et.gov.ethgov.payments.domain.cutoff.CutoffId;
import et.gov.ethgov.payments.domain.cutoff.CutoffRepository;
import java.util.Objects;

public final class GetCutoffUseCase {
    private final CutoffRepository repository;

    public GetCutoffUseCase(CutoffRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Cutoff execute(String rawId) {
        CutoffId id = CutoffId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cutoff not found: " + rawId));
    }
}
