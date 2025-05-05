package et.gov.ethgov.revenue.application.penalty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryPenaltyRepository;
import et.gov.ethgov.revenue.domain.penalty.Penalty;
import et.gov.ethgov.revenue.domain.penalty.PenaltyStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PenaltyApplicationTest {
    private InMemoryPenaltyRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPenaltyRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePenaltyUseCase(repository, clock);
        var activate = new ActivatePenaltyUseCase(repository, clock);
        var get = new GetPenaltyUseCase(repository);

        var id = create.execute(new CreatePenaltyUseCase.CreatePenaltyCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePenaltyUseCase.ActivatePenaltyCommand(
                null, null, null, null, id.toString(), null));

        Penalty loaded = get.execute(id.toString());
        assertEquals(PenaltyStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
