package et.gov.ethgov.document.application.signature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemorySignatureRepository;
import et.gov.ethgov.document.domain.signature.Signature;
import et.gov.ethgov.document.domain.signature.SignatureStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SignatureApplicationTest {
    private InMemorySignatureRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySignatureRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSignatureUseCase(repository, clock);
        var activate = new ActivateSignatureUseCase(repository, clock);
        var get = new GetSignatureUseCase(repository);

        var id = create.execute(new CreateSignatureUseCase.CreateSignatureCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSignatureUseCase.ActivateSignatureCommand(
                null, null, null, null, id.toString(), null));

        Signature loaded = get.execute(id.toString());
        assertEquals(SignatureStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
