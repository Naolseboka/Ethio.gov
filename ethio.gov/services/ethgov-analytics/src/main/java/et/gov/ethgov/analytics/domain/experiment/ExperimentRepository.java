package et.gov.ethgov.analytics.domain.experiment;

import java.util.List;
import java.util.Optional;

public interface ExperimentRepository {
    Experiment save(Experiment aggregate);

    Optional<Experiment> findById(ExperimentId id);

    Optional<Experiment> findByReferenceCode(String tenantId, String referenceCode);

    List<Experiment> findByTenant(String tenantId, int limit);

    void delete(ExperimentId id);
}
