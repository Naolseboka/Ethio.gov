package et.gov.ethgov.notification.application.emailbatch;

import et.gov.ethgov.notification.domain.emailbatch.Emailbatch;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchId;
import et.gov.ethgov.notification.domain.emailbatch.EmailbatchRepository;
import java.util.Objects;

public final class GetEmailbatchUseCase {
    private final EmailbatchRepository repository;

    public GetEmailbatchUseCase(EmailbatchRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Emailbatch execute(String rawId) {
        EmailbatchId id = EmailbatchId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("emailbatch not found: " + rawId));
    }
}
