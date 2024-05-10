package et.gov.ethgov.permits.application.sitevisit;

import et.gov.ethgov.permits.domain.sitevisit.Sitevisit;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitId;
import et.gov.ethgov.permits.domain.sitevisit.SitevisitRepository;
import java.util.Objects;

public final class GetSitevisitUseCase {
    private final SitevisitRepository repository;

    public GetSitevisitUseCase(SitevisitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Sitevisit execute(String rawId) {
        SitevisitId id = SitevisitId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("sitevisit not found: " + rawId));
    }
}
