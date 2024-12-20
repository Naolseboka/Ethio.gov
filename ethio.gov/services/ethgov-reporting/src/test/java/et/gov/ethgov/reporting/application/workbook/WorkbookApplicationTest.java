package et.gov.ethgov.reporting.application.workbook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemoryWorkbookRepository;
import et.gov.ethgov.reporting.domain.workbook.Workbook;
import et.gov.ethgov.reporting.domain.workbook.WorkbookStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkbookApplicationTest {
    private InMemoryWorkbookRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWorkbookRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateWorkbookUseCase(repository, clock);
        var activate = new ActivateWorkbookUseCase(repository, clock);
        var get = new GetWorkbookUseCase(repository);

        var id = create.execute(new CreateWorkbookUseCase.CreateWorkbookCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateWorkbookUseCase.ActivateWorkbookCommand(
                null, null, null, null, id.toString(), null));

        Workbook loaded = get.execute(id.toString());
        assertEquals(WorkbookStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
