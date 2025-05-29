package et.gov.ethgov.search.application.document;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.search.domain.document.SearchDocumentRepository;
import et.gov.ethgov.search.domain.document.SearchHit;
import et.gov.ethgov.search.domain.document.SourceType;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuerySearchDocumentsUseCase {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    private final SearchDocumentRepository documents;

    public QuerySearchDocumentsUseCase(SearchDocumentRepository documents) {
        this.documents = documents;
    }

    @Transactional(readOnly = true)
    public List<SearchHit> execute(String tenantId, String query, String sourceTypesCsv, Integer limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query is required");
        }
        String q = query.trim();
        if (q.length() < 2 || q.length() > 200) {
            throw new IllegalArgumentException("query length must be between 2 and 200");
        }
        int resolvedLimit = limit == null ? DEFAULT_LIMIT : limit;
        if (resolvedLimit < 1 || resolvedLimit > MAX_LIMIT) {
            throw new IllegalArgumentException("limit must be between 1 and " + MAX_LIMIT);
        }
        Set<SourceType> types = parseTypes(sourceTypesCsv);
        return documents.search(TenantId.of(tenantId), q, types, resolvedLimit);
    }

    private static Set<SourceType> parseTypes(String sourceTypesCsv) {
        if (sourceTypesCsv == null || sourceTypesCsv.isBlank()) {
            return EnumSet.noneOf(SourceType.class);
        }
        return Arrays.stream(sourceTypesCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(SourceType::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(SourceType.class)));
    }
}
