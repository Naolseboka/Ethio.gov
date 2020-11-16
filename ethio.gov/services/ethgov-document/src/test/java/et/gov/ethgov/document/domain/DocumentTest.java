package et.gov.ethgov.document.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.document.domain.document.ContentHash;
import et.gov.ethgov.document.domain.document.ContentType;
import et.gov.ethgov.document.domain.document.Document;
import et.gov.ethgov.document.domain.document.DocumentClassification;
import et.gov.ethgov.document.domain.document.DocumentDraftCreated;
import et.gov.ethgov.document.domain.document.DocumentOwnerRef;
import et.gov.ethgov.document.domain.document.DocumentStatus;
import et.gov.ethgov.document.domain.document.FileName;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DocumentTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");

    @Test
    void createsDraftAndCompletesUpload() {
        Document document = draft();
        assertThat(document.status()).isEqualTo(DocumentStatus.DRAFT);
        assertThat(document.pullDomainEvents()).anyMatch(DocumentDraftCreated.class::isInstance);
        document.completeUpload(
                12L,
                ContentHash.of("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"),
                now);
        assertThat(document.status()).isEqualTo(DocumentStatus.ACTIVE);
        assertThat(document.sizeBytes()).contains(12L);
    }

    @Test
    void archivesActiveDocumentAndBlocksMetadataEdits() {
        Document document = draft();
        document.completeUpload(
                4L,
                ContentHash.of("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"),
                now);
        document.pullDomainEvents();
        document.archive(now);
        assertThat(document.status()).isEqualTo(DocumentStatus.ARCHIVED);
        assertThatThrownBy(() -> document.updateMetadata(
                        "x", null, DocumentClassification.INTERNAL, now))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Archived");
    }

    @Test
    void rejectsUnsupportedContentType() {
        assertThatThrownBy(() -> ContentType.of("application/x-msdownload"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsOversizedDraft() {
        assertThatThrownBy(() -> Document.createDraft(
                        TenantId.generate(),
                        "Title",
                        null,
                        DocumentClassification.INTERNAL,
                        ContentType.of("application/pdf"),
                        FileName.of("a.pdf"),
                        null,
                        null,
                        Document.MAX_SIZE_BYTES + 1,
                        now))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Document draft() {
        return Document.createDraft(
                TenantId.generate(),
                "Land Title Scan",
                "Kebele copy",
                DocumentClassification.CONFIDENTIAL,
                ContentType.of("application/pdf"),
                FileName.of("title.pdf"),
                DocumentOwnerRef.of("CITIZEN", "cit_11111111-1111-4111-8111-111111111111"),
                "usr_11111111-1111-4111-8111-111111111111",
                1024,
                now);
    }
}
