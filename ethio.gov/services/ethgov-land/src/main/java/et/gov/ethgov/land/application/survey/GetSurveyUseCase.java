package et.gov.ethgov.land.application.survey;

import et.gov.ethgov.land.domain.survey.Survey;
import et.gov.ethgov.land.domain.survey.SurveyId;
import et.gov.ethgov.land.domain.survey.SurveyRepository;
import java.util.Objects;

public final class GetSurveyUseCase {
    private final SurveyRepository repository;

    public GetSurveyUseCase(SurveyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Survey execute(String rawId) {
        SurveyId id = SurveyId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("survey not found: " + rawId));
    }
}
