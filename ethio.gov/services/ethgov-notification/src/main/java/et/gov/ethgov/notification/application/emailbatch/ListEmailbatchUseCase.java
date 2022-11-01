package et.gov.ethgov.notification.application.emailbatch;

import et.gov.ethgov.notification.domain.emailbatch.Emailbatch;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchRepository;
import java.util.List;
import java.util.Objects;

public final class ListEmailbatchUseCase {
    private final EmailbatchRepository repository;

    public ListEmailbatchUseCase(EmailbatchRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Emailbatch> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
