package et.gov.ethgov.gateway.application.mtls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryMtlsRepository;
import et.gov.ethgov.gateway.domain.mtls.Mtls;
import et.gov.ethgov.gateway.domain.mtls.MtlsStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MtlsApplicationTest {
    private InMemoryMtlsRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryMtlsRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateMtlsUseCase(repository, clock);
        var activate = new ActivateMtlsUseCase(repository, clock);
        var get = new GetMtlsUseCase(repository);

        var id = create.execute(new CreateMtlsUseCase.CreateMtlsCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateMtlsUseCase.ActivateMtlsCommand(
                null, null, null, null, id.toString(), null));

        Mtls loaded = get.execute(id.toString());
        assertEquals(MtlsStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
