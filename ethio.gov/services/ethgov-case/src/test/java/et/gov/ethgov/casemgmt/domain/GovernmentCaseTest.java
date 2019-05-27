package et.gov.ethgov.casemgmt.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.casemgmt.domain.casefile.CaseNumber;
import et.gov.ethgov.casemgmt.domain.casefile.CaseOpened;
import et.gov.ethgov.casemgmt.domain.casefile.CaseParty;
import et.gov.ethgov.casemgmt.domain.casefile.CasePriority;
import et.gov.ethgov.casemgmt.domain.casefile.CaseStatus;
import et.gov.ethgov.casemgmt.domain.casefile.CaseType;
import et.gov.ethgov.casemgmt.domain.casefile.GovernmentCase;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class GovernmentCaseTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final String user = "usr_11111111-1111-4111-8111-111111111111";

    @Test
    void opensAssignsAndCloses() {
        GovernmentCase governmentCase = open();
        assertThat(governmentCase.status()).isEqualTo(CaseStatus.OPEN);
        assertThat(governmentCase.pullDomainEvents()).anyMatch(CaseOpened.class::isInstance);

        governmentCase.assign(user, now);
        assertThat(governmentCase.status()).isEqualTo(CaseStatus.IN_PROGRESS);

        governmentCase.changeStatus(CaseStatus.RESOLVED, now);
        governmentCase.changeStatus(CaseStatus.CLOSED, now);
        assertThat(governmentCase.status()).isEqualTo(CaseStatus.CLOSED);
        assertThatThrownBy(() -> governmentCase.addNote(user, "late note", now))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void linksWorkflowAndAttachesDocumentOnce() {
        GovernmentCase governmentCase = open();
        String wpi = "wpi_11111111-1111-4111-8111-111111111111";
        String doc = "doc_11111111-1111-4111-8111-111111111111";
        governmentCase.linkWorkflow(wpi, now);
        governmentCase.attachDocument(doc, now);
        governmentCase.attachDocument(doc, now);
        assertThat(governmentCase.documentIds()).containsExactly(doc);
        assertThatThrownBy(() -> governmentCase.linkWorkflow(
                        "wpi_22222222-2222-4222-8222-222222222222", now))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsCaseWithoutParty() {
        assertThatThrownBy(() -> CaseParty.of(null, null)).isInstanceOf(IllegalArgumentException.class);
    }

    private GovernmentCase open() {
        return GovernmentCase.open(
                TenantId.generate(),
                CaseNumber.allocate(2024, 1),
                CaseType.BUSINESS_LICENSE,
                "Shop license application",
                "Bole subcity",
                CasePriority.NORMAL,
                CaseParty.of("cit_11111111-1111-4111-8111-111111111111", null),
                null,
                now);
    }
}
