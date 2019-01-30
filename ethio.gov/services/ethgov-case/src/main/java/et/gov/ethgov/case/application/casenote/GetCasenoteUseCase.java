package et.gov.ethgov.case.application.casenote;

import et.gov.ethgov.case.domain.casenote.Casenote;
import et.gov.ethgov.case.domain.casenote.CasenoteId;
import et.gov.ethgov.case.domain.casenote.CasenoteRepository;
import java.util.Objects;

public final class GetCasenoteUseCase {
    private final CasenoteRepository repository;

    public GetCasenoteUseCase(CasenoteRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Casenote execute(String rawId) {
        CasenoteId id = CasenoteId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("casenote not found: " + rawId));
    }
}
