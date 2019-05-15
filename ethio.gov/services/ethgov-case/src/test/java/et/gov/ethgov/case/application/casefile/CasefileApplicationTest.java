package et.gov.ethgov.case.application.casefile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryCasefileRepository;
import et.gov.ethgov.case.domain.casefile.Casefile;
import et.gov.ethgov.case.domain.casefile.CasefileStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CasefileApplicationTest {
    private InMemoryCasefileRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCasefileRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCasefileUseCase(repository, clock);
        var activate = new ActivateCasefileUseCase(repository, clock);
        var get = new GetCasefileUseCase(repository);

        var id = create.execute(new CreateCasefileUseCase.CreateCasefileCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCasefileUseCase.ActivateCasefileCommand(
                null, null, null, null, id.toString(), null));

        Casefile loaded = get.execute(id.toString());
        assertEquals(CasefileStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
