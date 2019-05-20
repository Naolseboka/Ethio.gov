package et.gov.ethgov.case.application.casenote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryCasenoteRepository;
import et.gov.ethgov.case.domain.casenote.Casenote;
import et.gov.ethgov.case.domain.casenote.CasenoteStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CasenoteApplicationTest {
    private InMemoryCasenoteRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCasenoteRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCasenoteUseCase(repository, clock);
        var activate = new ActivateCasenoteUseCase(repository, clock);
        var get = new GetCasenoteUseCase(repository);

        var id = create.execute(new CreateCasenoteUseCase.CreateCasenoteCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCasenoteUseCase.ActivateCasenoteCommand(
                null, null, null, null, id.toString(), null));

        Casenote loaded = get.execute(id.toString());
        assertEquals(CasenoteStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
