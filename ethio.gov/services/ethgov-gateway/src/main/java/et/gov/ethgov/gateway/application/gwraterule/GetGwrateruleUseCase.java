package et.gov.ethgov.gateway.application.gwraterule;

import et.gov.ethgov.gateway.domain.gwraterule.Gwraterule;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleId;
import et.gov.ethgov.gateway.domain.gwraterule.GwrateruleRepository;
import java.util.Objects;

public final class GetGwrateruleUseCase {
    private final GwrateruleRepository repository;

    public GetGwrateruleUseCase(GwrateruleRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Gwraterule execute(String rawId) {
        GwrateruleId id = GwrateruleId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("gwraterule not found: " + rawId));
    }
}
