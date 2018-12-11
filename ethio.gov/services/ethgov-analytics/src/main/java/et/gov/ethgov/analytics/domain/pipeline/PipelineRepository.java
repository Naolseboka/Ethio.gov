package et.gov.ethgov.analytics.domain.pipeline;

import java.util.List;
import java.util.Optional;

public interface PipelineRepository {
    Pipeline save(Pipeline aggregate);

    Optional<Pipeline> findById(PipelineId id);

    Optional<Pipeline> findByReferenceCode(String tenantId, String referenceCode);

    List<Pipeline> findByTenant(String tenantId, int limit);

    void delete(PipelineId id);
}
