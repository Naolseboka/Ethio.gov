package et.gov.ethgov.permits.application.demolition;

import et.gov.ethgov.permits.domain.demolition.Demolition;
import et.gov.ethgov.permits.domain.demolition.DemolitionId;
import et.gov.ethgov.permits.domain.demolition.DemolitionRepository;
import java.util.Objects;

public final class GetDemolitionUseCase {
    private final DemolitionRepository repository;

    public GetDemolitionUseCase(DemolitionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Demolition execute(String rawId) {
        DemolitionId id = DemolitionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("demolition not found: " + rawId));
    }
}
