package et.gov.ethgov.land.application.survey;

import et.gov.ethgov.land.domain.survey.Survey;
import et.gov.ethgov.land.domain.survey.SurveyId;
import et.gov.ethgov.land.domain.survey.SurveyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateSurveyUseCase {
    private final SurveyRepository repository;
    private final Clock clock;

    public ActivateSurveyUseCase(SurveyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SurveyId execute(ActivateSurveyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SurveyId id = SurveyId.parse(command.surveyId());
        Survey aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("survey not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateSurveyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String surveyId,
            String reason) {}
}
