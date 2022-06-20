package et.gov.ethgov.licensing.application.holder;

import et.gov.ethgov.licensing.domain.holder.Holder;
import et.gov.ethgov.licensing.domain.holder.HolderId;
import et.gov.ethgov.licensing.domain.holder.HolderRepository;
import java.util.Objects;

public final class GetHolderUseCase {
    private final HolderRepository repository;

    public GetHolderUseCase(HolderRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Holder execute(String rawId) {
        HolderId id = HolderId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("holder not found: " + rawId));
    }
}
