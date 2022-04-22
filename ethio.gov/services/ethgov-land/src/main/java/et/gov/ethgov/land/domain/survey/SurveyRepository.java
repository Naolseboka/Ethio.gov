package et.gov.ethgov.land.domain.survey;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository {
    Survey save(Survey aggregate);

    Optional<Survey> findById(SurveyId id);

    Optional<Survey> findByReferenceCode(String tenantId, String referenceCode);

    List<Survey> findByTenant(String tenantId, int limit);

    void delete(SurveyId id);
}
