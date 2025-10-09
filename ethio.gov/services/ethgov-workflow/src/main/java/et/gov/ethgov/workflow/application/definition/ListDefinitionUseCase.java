package et.gov.ethgov.workflow.application.definition;

import et.gov.ethgov.workflow.domain.definition.Definition;
import et.gov.ethgov.workflow.domain.definition.DefinitionRepository;
import java.util.List;
import java.util.Objects;

public final class ListDefinitionUseCase {
    private final DefinitionRepository repository;

    public ListDefinitionUseCase(DefinitionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Definition> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
