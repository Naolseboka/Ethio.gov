package et.gov.ethgov.analytics.application.pipeline;

import et.gov.ethgov.analytics.domain.pipeline.Pipeline;
import et.gov.ethgov.analytics.domain.pipeline.PipelineId;
import et.gov.ethgov.analytics.domain.pipeline.PipelineRepository;
import java.util.Objects;

public final class GetPipelineUseCase {
    private final PipelineRepository repository;

    public GetPipelineUseCase(PipelineRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Pipeline execute(String rawId) {
        PipelineId id = PipelineId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("pipeline not found: " + rawId));
    }
}
