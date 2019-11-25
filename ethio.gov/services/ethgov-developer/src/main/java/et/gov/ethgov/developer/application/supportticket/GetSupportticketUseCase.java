package et.gov.ethgov.developer.application.supportticket;

import et.gov.ethgov.developer.domain.supportticket.Supportticket;
import et.gov.ethgov.developer.domain.supportticket.SupportticketId;
import et.gov.ethgov.developer.domain.supportticket.SupportticketRepository;
import java.util.Objects;

public final class GetSupportticketUseCase {
    private final SupportticketRepository repository;

    public GetSupportticketUseCase(SupportticketRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Supportticket execute(String rawId) {
        SupportticketId id = SupportticketId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("supportticket not found: " + rawId));
    }
}
