package et.gov.ethgov.document.application.template;

import et.gov.ethgov.document.domain.template.Template;
import et.gov.ethgov.document.domain.template.TemplateId;
import et.gov.ethgov.document.domain.template.TemplateRepository;
import java.util.Objects;

public final class GetTemplateUseCase {
    private final TemplateRepository repository;

    public GetTemplateUseCase(TemplateRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Template execute(String rawId) {
        TemplateId id = TemplateId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("template not found: " + rawId));
    }
}
