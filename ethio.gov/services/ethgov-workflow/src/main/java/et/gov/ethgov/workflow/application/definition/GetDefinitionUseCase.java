package et.gov.ethgov.workflow.application.definition;

import et.gov.ethgov.workflow.domain.definition.Definition;
import et.gov.ethgov.workflow.domain.definition.DefinitionId;
import et.gov.ethgov.workflow.domain.definition.DefinitionRepository;
import java.util.Objects;

public final class GetDefinitionUseCase {
    private final DefinitionRepository repository;

    public GetDefinitionUseCase(DefinitionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Definition execute(String rawId) {
        DefinitionId id = DefinitionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("definition not found: " + rawId));
    }
}
