package et.gov.ethgov.workflow.application.instance;

import et.gov.ethgov.workflow.domain.instance.Instance;
import et.gov.ethgov.workflow.domain.instance.InstanceId;
import et.gov.ethgov.workflow.domain.instance.InstanceRepository;
import java.util.Objects;

public final class GetInstanceUseCase {
    private final InstanceRepository repository;

    public GetInstanceUseCase(InstanceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Instance execute(String rawId) {
        InstanceId id = InstanceId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("instance not found: " + rawId));
    }
}
