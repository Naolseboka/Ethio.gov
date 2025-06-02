package et.gov.ethgov.search.application.document;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.search.application.SearchEventPublisher;
import et.gov.ethgov.search.domain.document.SearchDocument;
import et.gov.ethgov.search.domain.document.SearchDocumentId;
import et.gov.ethgov.search.domain.document.SearchDocumentRepository;
import et.gov.ethgov.search.domain.document.SearchLocale;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateSearchDocumentUseCase {

    private final SearchDocumentRepository documents;
    private final SearchEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateSearchDocumentUseCase(
            SearchDocumentRepository documents, SearchEventPublisher eventPublisher, Clock clock) {
        this.documents = documents;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        SearchDocument document = documents
                .findById(SearchDocumentId.of(command.searchDocumentId()))
                .orElseThrow(() -> new DomainException(
                        "SEARCH_DOCUMENT_NOT_FOUND",
                        "Search Document Not Found",
                        "Search document does not exist",
                        404));
        document.update(
                command.title(),
                command.summary(),
                command.bodyText(),
                SearchLocale.of(command.locale()),
                command.tagsJson(),
                clock.instant());
        documents.save(document);
        eventPublisher.publishAll(document.pullDomainEvents());
    }

    public record Command(
            String searchDocumentId,
            String title,
            String summary,
            String bodyText,
            String locale,
            String tagsJson) {}
}
