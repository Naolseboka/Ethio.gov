package et.gov.ethgov.case.application.caseassignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryCaseassignmentRepository;
import et.gov.ethgov.case.domain.caseassignment.Caseassignment;
import et.gov.ethgov.case.domain.caseassignment.CaseassignmentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CaseassignmentApplicationTest {
    private InMemoryCaseassignmentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCaseassignmentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCaseassignmentUseCase(repository, clock);
        var activate = new ActivateCaseassignmentUseCase(repository, clock);
        var get = new GetCaseassignmentUseCase(repository);

        var id = create.execute(new CreateCaseassignmentUseCase.CreateCaseassignmentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCaseassignmentUseCase.ActivateCaseassignmentCommand(
                null, null, null, null, id.toString(), null));

        Caseassignment loaded = get.execute(id.toString());
        assertEquals(CaseassignmentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
