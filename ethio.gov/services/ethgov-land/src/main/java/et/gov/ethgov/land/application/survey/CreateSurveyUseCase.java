package et.gov.ethgov.land.application.survey;

import et.gov.ethgov.land.domain.survey.Survey;
import et.gov.ethgov.land.domain.survey.SurveyId;
import et.gov.ethgov.land.domain.survey.SurveyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSurveyUseCase {
    private final SurveyRepository repository;
    private final Clock clock;

    public CreateSurveyUseCase(SurveyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SurveyId execute(CreateSurveyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Survey aggregate = Survey.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSurveyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String surveyId,
            String reason) {}
}
