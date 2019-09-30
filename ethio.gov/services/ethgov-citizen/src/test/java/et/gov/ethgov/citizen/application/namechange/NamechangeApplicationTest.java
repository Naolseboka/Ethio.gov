package et.gov.ethgov.citizen.application.namechange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryNamechangeRepository;
import et.gov.ethgov.citizen.domain.namechange.Namechange;
import et.gov.ethgov.citizen.domain.namechange.NamechangeStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NamechangeApplicationTest {
    private InMemoryNamechangeRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNamechangeRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateNamechangeUseCase(repository, clock);
        var activate = new ActivateNamechangeUseCase(repository, clock);
        var get = new GetNamechangeUseCase(repository);

        var id = create.execute(new CreateNamechangeUseCase.CreateNamechangeCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateNamechangeUseCase.ActivateNamechangeCommand(
                null, null, null, null, id.toString(), null));

        Namechange loaded = get.execute(id.toString());
        assertEquals(NamechangeStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
