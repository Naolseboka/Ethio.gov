package et.gov.ethgov.directory.application.servicedesk;

import et.gov.ethgov.directory.domain.servicedesk.Servicedesk;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskId;
import et.gov.ethgov.directory.domain.servicedesk.ServicedeskRepository;
import java.util.Objects;

public final class GetServicedeskUseCase {
    private final ServicedeskRepository repository;

    public GetServicedeskUseCase(ServicedeskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Servicedesk execute(String rawId) {
        ServicedeskId id = ServicedeskId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("servicedesk not found: " + rawId));
    }
}
