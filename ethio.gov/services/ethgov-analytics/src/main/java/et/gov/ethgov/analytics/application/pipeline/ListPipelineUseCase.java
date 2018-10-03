package et.gov.ethgov.analytics.application.pipeline;

import et.gov.ethgov.analytics.domain.pipeline.Pipeline;
import et.gov.ethgov.analytics.domain.pipeline.PipelineRepository;
import java.util.List;
import java.util.Objects;

public final class ListPipelineUseCase {
    private final PipelineRepository repository;

    public ListPipelineUseCase(PipelineRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Pipeline> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
