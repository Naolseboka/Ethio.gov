package et.gov.ethgov.land.application.survey;

import et.gov.ethgov.land.domain.survey.Survey;
import et.gov.ethgov.land.domain.survey.SurveyId;
import et.gov.ethgov.land.domain.survey.SurveyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSurveyUseCase {
    private final SurveyRepository repository;
    private final Clock clock;

    public UpdateSurveyUseCase(SurveyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SurveyId execute(UpdateSurveyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SurveyId id = SurveyId.parse(command.surveyId());
        Survey aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("survey not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSurveyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String surveyId,
            String reason) {}
}
