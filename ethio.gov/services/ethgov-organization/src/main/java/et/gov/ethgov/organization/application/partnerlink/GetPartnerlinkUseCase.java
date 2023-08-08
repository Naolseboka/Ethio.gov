package et.gov.ethgov.organization.application.partnerlink;

import et.gov.ethgov.organization.domain.partnerlink.Partnerlink;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkId;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkRepository;
import java.util.Objects;

public final class GetPartnerlinkUseCase {
    private final PartnerlinkRepository repository;

    public GetPartnerlinkUseCase(PartnerlinkRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Partnerlink execute(String rawId) {
        PartnerlinkId id = PartnerlinkId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("partnerlink not found: " + rawId));
    }
}
