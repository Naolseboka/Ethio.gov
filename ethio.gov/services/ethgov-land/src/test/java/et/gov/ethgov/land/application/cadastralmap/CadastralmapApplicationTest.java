package et.gov.ethgov.land.application.cadastralmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryCadastralmapRepository;
import et.gov.ethgov.land.domain.cadastralmap.Cadastralmap;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CadastralmapApplicationTest {
    private InMemoryCadastralmapRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCadastralmapRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCadastralmapUseCase(repository, clock);
        var activate = new ActivateCadastralmapUseCase(repository, clock);
        var get = new GetCadastralmapUseCase(repository);

        var id = create.execute(new CreateCadastralmapUseCase.CreateCadastralmapCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCadastralmapUseCase.ActivateCadastralmapCommand(
                null, null, null, null, id.toString(), null));

        Cadastralmap loaded = get.execute(id.toString());
        assertEquals(CadastralmapStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
