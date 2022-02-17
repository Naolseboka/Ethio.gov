package et.gov.ethgov.land.application.survey;

import et.gov.ethgov.land.domain.survey.Survey;
import et.gov.ethgov.land.domain.survey.SurveyRepository;
import java.util.List;
import java.util.Objects;

public final class ListSurveyUseCase {
    private final SurveyRepository repository;

    public ListSurveyUseCase(SurveyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Survey> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
