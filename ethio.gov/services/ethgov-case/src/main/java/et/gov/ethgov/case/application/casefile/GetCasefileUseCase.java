package et.gov.ethgov.case.application.casefile;

import et.gov.ethgov.case.domain.casefile.Casefile;
import et.gov.ethgov.case.domain.casefile.CasefileId;
import et.gov.ethgov.case.domain.casefile.CasefileRepository;
import java.util.Objects;

public final class GetCasefileUseCase {
    private final CasefileRepository repository;

    public GetCasefileUseCase(CasefileRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Casefile execute(String rawId) {
        CasefileId id = CasefileId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("casefile not found: " + rawId));
    }
}
