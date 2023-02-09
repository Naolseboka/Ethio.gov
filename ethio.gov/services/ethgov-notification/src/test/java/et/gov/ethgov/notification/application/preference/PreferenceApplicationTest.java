package et.gov.ethgov.notification.application.preference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemoryPreferenceRepository;
import et.gov.ethgov.notification.domain.preference.Preference;
import et.gov.ethgov.notification.domain.preference.PreferenceStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PreferenceApplicationTest {
    private InMemoryPreferenceRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPreferenceRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreatePreferenceUseCase(repository, clock);
        var activate = new ActivatePreferenceUseCase(repository, clock);
        var get = new GetPreferenceUseCase(repository);

        var id = create.execute(new CreatePreferenceUseCase.CreatePreferenceCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivatePreferenceUseCase.ActivatePreferenceCommand(
                null, null, null, null, id.toString(), null));

        Preference loaded = get.execute(id.toString());
        assertEquals(PreferenceStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
