package et.gov.ethgov.revenue.application.writeoff;

import et.gov.ethgov.revenue.domain.writeoff.Writeoff;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffId;
import et.gov.ethgov.revenue.domain.writeoff.WriteoffRepository;
import java.util.Objects;

public final class GetWriteoffUseCase {
    private final WriteoffRepository repository;

    public GetWriteoffUseCase(WriteoffRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Writeoff execute(String rawId) {
        WriteoffId id = WriteoffId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("writeoff not found: " + rawId));
    }
}
