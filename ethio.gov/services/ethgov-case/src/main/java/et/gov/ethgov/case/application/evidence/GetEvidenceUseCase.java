package et.gov.ethgov.case.application.evidence;

import et.gov.ethgov.case.domain.evidence.Evidence;
import et.gov.ethgov.case.domain.evidence.EvidenceId;
import et.gov.ethgov.case.domain.evidence.EvidenceRepository;
import java.util.Objects;

public final class GetEvidenceUseCase {
    private final EvidenceRepository repository;

    public GetEvidenceUseCase(EvidenceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Evidence execute(String rawId) {
        EvidenceId id = EvidenceId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("evidence not found: " + rawId));
    }
}
