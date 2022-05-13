package et.gov.ethgov.land.application.survey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemorySurveyRepository;
import et.gov.ethgov.land.domain.survey.Survey;
import et.gov.ethgov.land.domain.survey.SurveyStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SurveyApplicationTest {
    private InMemorySurveyRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySurveyRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSurveyUseCase(repository, clock);
        var activate = new ActivateSurveyUseCase(repository, clock);
        var get = new GetSurveyUseCase(repository);

        var id = create.execute(new CreateSurveyUseCase.CreateSurveyCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSurveyUseCase.ActivateSurveyCommand(
                null, null, null, null, id.toString(), null));

        Survey loaded = get.execute(id.toString());
        assertEquals(SurveyStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
