package et.gov.ethgov.case.application.appeal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryAppealRepository;
import et.gov.ethgov.case.domain.appeal.Appeal;
import et.gov.ethgov.case.domain.appeal.AppealStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppealApplicationTest {
    private InMemoryAppealRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAppealRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAppealUseCase(repository, clock);
        var activate = new ActivateAppealUseCase(repository, clock);
        var get = new GetAppealUseCase(repository);

        var id = create.execute(new CreateAppealUseCase.CreateAppealCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAppealUseCase.ActivateAppealCommand(
                null, null, null, null, id.toString(), null));

        Appeal loaded = get.execute(id.toString());
        assertEquals(AppealStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
