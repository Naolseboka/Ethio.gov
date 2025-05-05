package et.gov.ethgov.revenue.application.installment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryInstallmentRepository;
import et.gov.ethgov.revenue.domain.installment.Installment;
import et.gov.ethgov.revenue.domain.installment.InstallmentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstallmentApplicationTest {
    private InMemoryInstallmentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryInstallmentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateInstallmentUseCase(repository, clock);
        var activate = new ActivateInstallmentUseCase(repository, clock);
        var get = new GetInstallmentUseCase(repository);

        var id = create.execute(new CreateInstallmentUseCase.CreateInstallmentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateInstallmentUseCase.ActivateInstallmentCommand(
                null, null, null, null, id.toString(), null));

        Installment loaded = get.execute(id.toString());
        assertEquals(InstallmentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
