package et.gov.ethgov.notification.application.dispatch;

import et.gov.ethgov.notification.domain.dispatch.Dispatch;
import et.gov.ethgov.notification.domain.dispatch.DispatchId;
import et.gov.ethgov.notification.domain.dispatch.DispatchRepository;
import java.util.Objects;

public final class GetDispatchUseCase {
    private final DispatchRepository repository;

    public GetDispatchUseCase(DispatchRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Dispatch execute(String rawId) {
        DispatchId id = DispatchId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dispatch not found: " + rawId));
    }
}
