package et.gov.ethgov.search.application.document;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import et.gov.ethgov.search.application.SearchEventPublisher;
import et.gov.ethgov.search.domain.document.DocumentNumber;
import et.gov.ethgov.search.domain.document.DocumentNumberSequence;
import et.gov.ethgov.search.domain.document.SearchDocument;
import et.gov.ethgov.search.domain.document.SearchDocumentId;
import et.gov.ethgov.search.domain.document.SearchDocumentRepository;
import et.gov.ethgov.search.domain.document.SearchDocumentStatus;
import et.gov.ethgov.search.domain.document.SearchLocale;
import et.gov.ethgov.search.domain.document.SourceReference;
import et.gov.ethgov.search.domain.document.SourceType;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndexSearchDocumentUseCase {

    private final SearchDocumentRepository documents;
    private final DocumentNumberSequence sequences;
    private final SearchEventPublisher eventPublisher;
    private final Clock clock;

    public IndexSearchDocumentUseCase(
            SearchDocumentRepository documents,
            DocumentNumberSequence sequences,
            SearchEventPublisher eventPublisher,
            Clock clock) {
        this.documents = documents;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public SearchDocumentId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        SourceType sourceType = SourceType.valueOf(command.sourceType());
        SourceReference sourceId = SourceReference.of(command.sourceId());
        SearchLocale locale =
                command.locale() == null || command.locale().isBlank()
                        ? SearchLocale.english()
                        : SearchLocale.of(command.locale());

        Optional<SearchDocument> existing =
                documents.findByTenantAndSource(tenantId, sourceType, sourceId);
        if (existing.isPresent()) {
            SearchDocument document = existing.get();
            if (document.status() == SearchDocumentStatus.RETIRED) {
                document.reactivate(
                        command.title(),
                        command.summary(),
                        command.bodyText(),
                        locale,
                        command.tagsJson(),
                        clock.instant());
            } else {
                document.update(
                        command.title(),
                        command.summary(),
                        command.bodyText(),
                        locale,
                        command.tagsJson(),
                        clock.instant());
            }
            documents.save(document);
            eventPublisher.publishAll(document.pullDomainEvents());
            return document.id();
        }

        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        DocumentNumber documentNumber =
                DocumentNumber.allocate(year, sequences.nextSequence(tenantId, year));
        SearchDocument document = SearchDocument.index(
                tenantId,
                documentNumber,
                sourceType,
                sourceId,
                command.title(),
                command.summary(),
                command.bodyText(),
                locale,
                command.tagsJson(),
                clock.instant());
        documents.save(document);
        eventPublisher.publishAll(document.pullDomainEvents());
        return document.id();
    }

    public record Command(
            String tenantId,
            String sourceType,
            String sourceId,
            String title,
            String summary,
            String bodyText,
            String locale,
            String tagsJson) {}
}
