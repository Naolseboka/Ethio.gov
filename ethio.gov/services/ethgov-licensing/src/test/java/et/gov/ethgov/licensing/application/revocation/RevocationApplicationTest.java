package et.gov.ethgov.licensing.application.revocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.licensing.adapter.out.persistence.InMemoryRevocationRepository;
import et.gov.ethgov.licensing.domain.revocation.Revocation;
import et.gov.ethgov.licensing.domain.revocation.RevocationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RevocationApplicationTest {
    private InMemoryRevocationRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRevocationRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateRevocationUseCase(repository, clock);
        var activate = new ActivateRevocationUseCase(repository, clock);
        var get = new GetRevocationUseCase(repository);

        var id = create.execute(new CreateRevocationUseCase.CreateRevocationCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateRevocationUseCase.ActivateRevocationCommand(
                null, null, null, null, id.toString(), null));

        Revocation loaded = get.execute(id.toString());
        assertEquals(RevocationStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
