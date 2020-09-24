package et.gov.ethgov.document.domain.document;

import java.util.Optional;

public interface DocumentRepository {

    Optional<Document> findById(DocumentId id);

    void save(Document document);
}
