package et.gov.ethgov.notification.domain.template;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository {
    Template save(Template aggregate);

    Optional<Template> findById(TemplateId id);

    Optional<Template> findByReferenceCode(String tenantId, String referenceCode);

    List<Template> findByTenant(String tenantId, int limit);

    void delete(TemplateId id);
}
