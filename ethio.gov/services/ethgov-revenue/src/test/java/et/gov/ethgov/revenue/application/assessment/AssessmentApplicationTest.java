package et.gov.ethgov.revenue.application.assessment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryAssessmentRepository;
import et.gov.ethgov.revenue.domain.assessment.Assessment;
import et.gov.ethgov.revenue.domain.assessment.AssessmentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssessmentApplicationTest {
    private InMemoryAssessmentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAssessmentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAssessmentUseCase(repository, clock);
        var activate = new ActivateAssessmentUseCase(repository, clock);
        var get = new GetAssessmentUseCase(repository);

        var id = create.execute(new CreateAssessmentUseCase.CreateAssessmentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAssessmentUseCase.ActivateAssessmentCommand(
                null, null, null, null, id.toString(), null));

        Assessment loaded = get.execute(id.toString());
        assertEquals(AssessmentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
