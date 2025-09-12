package et.gov.ethgov.search.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.search.domain.document.DocumentNumber;
import et.gov.ethgov.search.domain.document.SearchDocument;
import et.gov.ethgov.search.domain.document.SearchDocumentIndexed;
import et.gov.ethgov.search.domain.document.SearchDocumentStatus;
import et.gov.ethgov.search.domain.document.SearchLocale;
import et.gov.ethgov.search.domain.document.SourceReference;
import et.gov.ethgov.search.domain.document.SourceType;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class SearchDocumentTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void indexesUpdatesAndRetires() {
        SearchDocument document = indexCitizen("Abebe Kebede");
        assertThat(document.status()).isEqualTo(SearchDocumentStatus.ACTIVE);
        assertThat(document.pullDomainEvents()).anyMatch(SearchDocumentIndexed.class::isInstance);

        document.update(
                "Abebe Kebede Updated",
                "Citizen profile",
                "Abebe Kebede Addis Ababa Bole",
                SearchLocale.of("am"),
                "[\"citizen\"]",
                now.plusSeconds(10));
        assertThat(document.title()).contains("Updated");

        document.retire("source entity deleted", now.plusSeconds(20));
        assertThat(document.status()).isEqualTo(SearchDocumentStatus.RETIRED);
        assertThatThrownBy(() -> document.update(
                        "x", null, "body text here", SearchLocale.english(), "[]", now.plusSeconds(30)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void reactivatesRetiredDocument() {
        SearchDocument document = indexCitizen("Sara Alemu");
        document.retire("temporary withdrawal", now.plusSeconds(5));
        document.reactivate(
                "Sara Alemu",
                null,
                "Sara Alemu Hawassa",
                SearchLocale.english(),
                "[]",
                now.plusSeconds(10));
        assertThat(document.status()).isEqualTo(SearchDocumentStatus.ACTIVE);
    }

    private SearchDocument indexCitizen(String name) {
        return SearchDocument.index(
                TenantId.generate(),
                DocumentNumber.allocate(2024, 1),
                SourceType.CITIZEN,
                SourceReference.of("cit_11111111-1111-4111-8111-111111111111"),
                name,
                "Citizen registry entry",
                name + " Ethiopia",
                SearchLocale.of("en"),
                "[\"citizen\"]",
                now);
    }
}
