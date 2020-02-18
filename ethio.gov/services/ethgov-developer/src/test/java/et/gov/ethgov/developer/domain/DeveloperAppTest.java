package et.gov.ethgov.developer.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.developer.domain.app.AllowedScopes;
import et.gov.ethgov.developer.domain.app.AppNumber;
import et.gov.ethgov.developer.domain.app.AppSlug;
import et.gov.ethgov.developer.domain.app.DeveloperApp;
import et.gov.ethgov.developer.domain.app.DeveloperAppActivated;
import et.gov.ethgov.developer.domain.app.DeveloperAppRegistered;
import et.gov.ethgov.developer.domain.app.DeveloperAppStatus;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DeveloperAppTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void registersActivatesSuspendsAndRetires() {
        DeveloperApp app = register();
        assertThat(app.status()).isEqualTo(DeveloperAppStatus.DRAFT);
        assertThat(app.pullDomainEvents()).anyMatch(DeveloperAppRegistered.class::isInstance);

        app.activate(now.plusSeconds(10));
        assertThat(app.status()).isEqualTo(DeveloperAppStatus.ACTIVE);
        assertThat(app.pullDomainEvents()).anyMatch(DeveloperAppActivated.class::isInstance);

        app.suspend("policy review", now.plusSeconds(20));
        assertThat(app.status()).isEqualTo(DeveloperAppStatus.SUSPENDED);

        app.activate(now.plusSeconds(30));
        app.retire(now.plusSeconds(40));
        assertThat(app.status()).isEqualTo(DeveloperAppStatus.RETIRED);
        assertThatThrownBy(() -> app.cancel("too late", now.plusSeconds(50)))
                .isInstanceOf(DomainException.class);
    }

    private DeveloperApp register() {
        return DeveloperApp.register(
                TenantId.generate(),
                AppNumber.allocate(2024, 1),
                AppSlug.of("bole-partner-portal"),
                "Bole Partner Portal",
                "Municipal partner integration",
                "org_11111111-1111-4111-8111-111111111111",
                "dev@partner.et",
                AllowedScopes.of("[\"citizen.read\",\"opendata.read\"]"),
                now);
    }
}
