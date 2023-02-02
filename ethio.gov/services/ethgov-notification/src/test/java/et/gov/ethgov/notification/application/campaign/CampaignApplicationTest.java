package et.gov.ethgov.notification.application.campaign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.notification.adapter.out.persistence.InMemoryCampaignRepository;
import et.gov.ethgov.notification.domain.campaign.Campaign;
import et.gov.ethgov.notification.domain.campaign.CampaignStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CampaignApplicationTest {
    private InMemoryCampaignRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCampaignRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCampaignUseCase(repository, clock);
        var activate = new ActivateCampaignUseCase(repository, clock);
        var get = new GetCampaignUseCase(repository);

        var id = create.execute(new CreateCampaignUseCase.CreateCampaignCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCampaignUseCase.ActivateCampaignCommand(
                null, null, null, null, id.toString(), null));

        Campaign loaded = get.execute(id.toString());
        assertEquals(CampaignStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
