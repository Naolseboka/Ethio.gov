package et.gov.ethgov.case.application.party;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryPartyRepository;
import et.gov.ethgov.case.domain.party.Party;
import et.gov.ethgov.case.domain.party.PartyStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartyApplicationTest {
    private InMemoryPartyRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPartyRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePartyUseCase(repository, clock);
        var activate = new ActivatePartyUseCase(repository, clock);
        var get = new GetPartyUseCase(repository);

        var id = create.execute(new CreatePartyUseCase.CreatePartyCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePartyUseCase.ActivatePartyCommand(
                null, null, null, null, id.toString(), null));

        Party loaded = get.execute(id.toString());
        assertEquals(PartyStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
