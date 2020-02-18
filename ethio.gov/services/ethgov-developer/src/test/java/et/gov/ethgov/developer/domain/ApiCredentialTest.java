package et.gov.ethgov.developer.domain;

import static org.assertj.core.api.Assertions.assertThat;

import et.gov.ethgov.developer.domain.app.DeveloperAppId;
import et.gov.ethgov.developer.domain.credential.ApiCredential;
import et.gov.ethgov.developer.domain.credential.ApiCredentialIssued;
import et.gov.ethgov.developer.domain.credential.CredentialNumber;
import et.gov.ethgov.developer.domain.credential.CredentialStatus;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ApiCredentialTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void issuesAndRevokes() {
        ApiCredential credential = ApiCredential.issue(
                TenantId.generate(),
                CredentialNumber.allocate(2024, 1),
                DeveloperAppId.generate(),
                "egk_abcdef12",
                "a".repeat(64),
                "production key",
                now.plusSeconds(3600),
                now);
        assertThat(credential.status()).isEqualTo(CredentialStatus.ACTIVE);
        assertThat(credential.isUsableAt(now.plusSeconds(10))).isTrue();
        assertThat(credential.pullDomainEvents()).anyMatch(ApiCredentialIssued.class::isInstance);

        credential.revoke("rotated after incident", now.plusSeconds(20));
        assertThat(credential.status()).isEqualTo(CredentialStatus.REVOKED);
        assertThat(credential.isUsableAt(now.plusSeconds(30))).isFalse();
    }
}
