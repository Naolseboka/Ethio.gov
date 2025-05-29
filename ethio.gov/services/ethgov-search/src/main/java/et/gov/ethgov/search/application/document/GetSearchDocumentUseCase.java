package et.gov.ethgov.search.application.document;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.search.domain.document.DocumentNumber;
import et.gov.ethgov.search.domain.document.SearchDocument;
import et.gov.ethgov.search.domain.document.SearchDocumentId;
import et.gov.ethgov.search.domain.document.SearchDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetSearchDocumentUseCase {

    private final SearchDocumentRepository documents;

    public GetSearchDocumentUseCase(SearchDocumentRepository documents) {
        this.documents = documents;
    }

    @Transactional(readOnly = true)
    public SearchDocument byId(String searchDocumentId) {
        return documents
                .findById(SearchDocumentId.of(searchDocumentId))
                .orElseThrow(() -> new DomainException(
                        "SEARCH_DOCUMENT_NOT_FOUND",
                        "Search Document Not Found",
                        "Search document does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public SearchDocument byNumber(String tenantId, String documentNumber) {
        return documents
                .findByTenantAndDocumentNumber(TenantId.of(tenantId), DocumentNumber.of(documentNumber))
                .orElseThrow(() -> new DomainException(
                        "SEARCH_DOCUMENT_NOT_FOUND",
                        "Search Document Not Found",
                        "Search document does not exist",
                        404));
    }
}
