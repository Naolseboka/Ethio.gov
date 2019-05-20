package et.gov.ethgov.case.application.hearing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.case.adapter.out.persistence.InMemoryHearingRepository;
import et.gov.ethgov.case.domain.hearing.Hearing;
import et.gov.ethgov.case.domain.hearing.HearingStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HearingApplicationTest {
    private InMemoryHearingRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHearingRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateHearingUseCase(repository, clock);
        var activate = new ActivateHearingUseCase(repository, clock);
        var get = new GetHearingUseCase(repository);

        var id = create.execute(new CreateHearingUseCase.CreateHearingCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateHearingUseCase.ActivateHearingCommand(
                null, null, null, null, id.toString(), null));

        Hearing loaded = get.execute(id.toString());
        assertEquals(HearingStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
