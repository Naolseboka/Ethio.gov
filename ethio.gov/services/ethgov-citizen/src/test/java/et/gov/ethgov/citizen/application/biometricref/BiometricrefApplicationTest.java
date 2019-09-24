package et.gov.ethgov.citizen.application.biometricref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryBiometricrefRepository;
import et.gov.ethgov.citizen.domain.biometricref.Biometricref;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BiometricrefApplicationTest {
    private InMemoryBiometricrefRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBiometricrefRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateBiometricrefUseCase(repository, clock);
        var activate = new ActivateBiometricrefUseCase(repository, clock);
        var get = new GetBiometricrefUseCase(repository);

        var id = create.execute(new CreateBiometricrefUseCase.CreateBiometricrefCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateBiometricrefUseCase.ActivateBiometricrefCommand(
                null, null, null, null, id.toString(), null));

        Biometricref loaded = get.execute(id.toString());
        assertEquals(BiometricrefStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
