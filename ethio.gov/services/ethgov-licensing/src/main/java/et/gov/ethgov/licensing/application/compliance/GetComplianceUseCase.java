package et.gov.ethgov.licensing.application.compliance;

import et.gov.ethgov.licensing.domain.compliance.Compliance;
import et.gov.ethgov.licensing.domain.compliance.ComplianceId;
import et.gov.ethgov.licensing.domain.compliance.ComplianceRepository;
import java.util.Objects;

public final class GetComplianceUseCase {
    private final ComplianceRepository repository;

    public GetComplianceUseCase(ComplianceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Compliance execute(String rawId) {
        ComplianceId id = ComplianceId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("compliance not found: " + rawId));
    }
}
