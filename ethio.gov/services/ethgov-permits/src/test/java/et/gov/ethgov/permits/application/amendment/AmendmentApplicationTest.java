package et.gov.ethgov.permits.application.amendment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryAmendmentRepository;
import et.gov.ethgov.permits.domain.amendment.Amendment;
import et.gov.ethgov.permits.domain.amendment.AmendmentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AmendmentApplicationTest {
    private InMemoryAmendmentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAmendmentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateAmendmentUseCase(repository, clock);
        var activate = new ActivateAmendmentUseCase(repository, clock);
        var get = new GetAmendmentUseCase(repository);

        var id = create.execute(new CreateAmendmentUseCase.CreateAmendmentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateAmendmentUseCase.ActivateAmendmentCommand(
                null, null, null, null, id.toString(), null));

        Amendment loaded = get.execute(id.toString());
        assertEquals(AmendmentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
