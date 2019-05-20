package et.gov.ethgov.case.application.evidence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryEvidenceRepository;
import et.gov.ethgov.case.domain.evidence.Evidence;
import et.gov.ethgov.case.domain.evidence.EvidenceStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EvidenceApplicationTest {
    private InMemoryEvidenceRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEvidenceRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateEvidenceUseCase(repository, clock);
        var activate = new ActivateEvidenceUseCase(repository, clock);
        var get = new GetEvidenceUseCase(repository);

        var id = create.execute(new CreateEvidenceUseCase.CreateEvidenceCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateEvidenceUseCase.ActivateEvidenceCommand(
                null, null, null, null, id.toString(), null));

        Evidence loaded = get.execute(id.toString());
        assertEquals(EvidenceStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
