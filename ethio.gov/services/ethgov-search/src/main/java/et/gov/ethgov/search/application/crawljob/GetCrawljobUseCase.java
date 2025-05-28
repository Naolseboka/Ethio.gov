package et.gov.ethgov.search.application.crawljob;

import et.gov.ethgov.search.domain.crawljob.Crawljob;
import et.gov.ethgov.search.domain.crawljob.CrawljobId;
import et.gov.ethgov.search.domain.crawljob.CrawljobRepository;
import java.util.Objects;

public final class GetCrawljobUseCase {
    private final CrawljobRepository repository;

    public GetCrawljobUseCase(CrawljobRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Crawljob execute(String rawId) {
        CrawljobId id = CrawljobId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("crawljob not found: " + rawId));
    }
}
