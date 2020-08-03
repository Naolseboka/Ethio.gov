package et.gov.ethgov.document.application.document;

import et.gov.ethgov.document.domain.document.Document;
import et.gov.ethgov.document.domain.document.DocumentId;
import et.gov.ethgov.document.domain.document.DocumentRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDocumentUseCase {

    private final DocumentRepository documents;

    public GetDocumentUseCase(DocumentRepository documents) {
        this.documents = documents;
    }

    @Transactional(readOnly = true)
    public Document byId(String documentId) {
        return documents
                .findById(DocumentId.of(documentId))
                .orElseThrow(() -> new DomainException(
                        "DOCUMENT_NOT_FOUND",
                        "Document Not Found",
                        "Document does not exist",
                        404));
    }
}
