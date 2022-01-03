package et.gov.ethgov.identity.application.riskscore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.identity.adapter.out.persistence.InMemoryRiskscoreRepository;
import et.gov.ethgov.identity.domain.riskscore.Riskscore;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RiskscoreApplicationTest {
    private InMemoryRiskscoreRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRiskscoreRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRiskscoreUseCase(repository, clock);
        var activate = new ActivateRiskscoreUseCase(repository, clock);
        var get = new GetRiskscoreUseCase(repository);

        var id = create.execute(new CreateRiskscoreUseCase.CreateRiskscoreCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRiskscoreUseCase.ActivateRiskscoreCommand(
                null, null, null, null, id.toString(), null));

        Riskscore loaded = get.execute(id.toString());
        assertEquals(RiskscoreStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
