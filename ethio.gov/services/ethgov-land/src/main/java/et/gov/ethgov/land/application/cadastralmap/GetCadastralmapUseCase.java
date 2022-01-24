package et.gov.ethgov.land.application.cadastralmap;

import et.gov.ethgov.land.domain.cadastralmap.Cadastralmap;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapId;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapRepository;
import java.util.Objects;

public final class GetCadastralmapUseCase {
    private final CadastralmapRepository repository;

    public GetCadastralmapUseCase(CadastralmapRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Cadastralmap execute(String rawId) {
        CadastralmapId id = CadastralmapId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cadastralmap not found: " + rawId));
    }
}
