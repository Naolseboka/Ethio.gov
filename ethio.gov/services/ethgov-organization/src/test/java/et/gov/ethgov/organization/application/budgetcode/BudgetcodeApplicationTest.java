package et.gov.ethgov.organization.application.budgetcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.organization.adapter.out.persistence.InMemoryBudgetcodeRepository;
import et.gov.ethgov.organization.domain.budgetcode.Budgetcode;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BudgetcodeApplicationTest {
    private InMemoryBudgetcodeRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBudgetcodeRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateBudgetcodeUseCase(repository, clock);
        var activate = new ActivateBudgetcodeUseCase(repository, clock);
        var get = new GetBudgetcodeUseCase(repository);

        var id = create.execute(new CreateBudgetcodeUseCase.CreateBudgetcodeCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateBudgetcodeUseCase.ActivateBudgetcodeCommand(
                null, null, null, null, id.toString(), null));

        Budgetcode loaded = get.execute(id.toString());
        assertEquals(BudgetcodeStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
