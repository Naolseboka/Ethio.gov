package et.gov.ethgov.citizen.application.educationrecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryEducationrecordRepository;
import et.gov.ethgov.citizen.domain.educationrecord.Educationrecord;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EducationrecordApplicationTest {
    private InMemoryEducationrecordRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEducationrecordRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateEducationrecordUseCase(repository, clock);
        var activate = new ActivateEducationrecordUseCase(repository, clock);
        var get = new GetEducationrecordUseCase(repository);

        var id = create.execute(new CreateEducationrecordUseCase.CreateEducationrecordCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateEducationrecordUseCase.ActivateEducationrecordCommand(
                null, null, null, null, id.toString(), null));

        Educationrecord loaded = get.execute(id.toString());
        assertEquals(EducationrecordStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
