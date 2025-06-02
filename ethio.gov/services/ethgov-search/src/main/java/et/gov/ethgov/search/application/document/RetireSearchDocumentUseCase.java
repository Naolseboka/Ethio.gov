package et.gov.ethgov.search.application.document;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.search.application.SearchEventPublisher;
import et.gov.ethgov.search.domain.document.SearchDocument;
import et.gov.ethgov.search.domain.document.SearchDocumentId;
import et.gov.ethgov.search.domain.document.SearchDocumentRepository;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetireSearchDocumentUseCase {

    private final SearchDocumentRepository documents;
    private final SearchEventPublisher eventPublisher;
    private final Clock clock;

    public RetireSearchDocumentUseCase(
            SearchDocumentRepository documents, SearchEventPublisher eventPublisher, Clock clock) {
        this.documents = documents;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String searchDocumentId, String reason) {
        Objects.requireNonNull(searchDocumentId, "searchDocumentId");
        SearchDocument document = documents
                .findById(SearchDocumentId.of(searchDocumentId))
                .orElseThrow(() -> new DomainException(
                        "SEARCH_DOCUMENT_NOT_FOUND",
                        "Search Document Not Found",
                        "Search document does not exist",
                        404));
        document.retire(reason, clock.instant());
        documents.save(document);
        eventPublisher.publishAll(document.pullDomainEvents());
    }
}
