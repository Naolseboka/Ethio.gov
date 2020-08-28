package et.gov.ethgov.document.application.template;

import et.gov.ethgov.document.domain.template.Template;
import et.gov.ethgov.document.domain.template.TemplateRepository;
import java.util.List;
import java.util.Objects;

public final class ListTemplateUseCase {
    private final TemplateRepository repository;

    public ListTemplateUseCase(TemplateRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Template> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
