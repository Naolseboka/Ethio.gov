package et.gov.ethgov.land.application.cadastralmap;

import et.gov.ethgov.land.domain.cadastralmap.Cadastralmap;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapId;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCadastralmapUseCase {
    private final CadastralmapRepository repository;
    private final Clock clock;

    public UpdateCadastralmapUseCase(CadastralmapRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CadastralmapId execute(UpdateCadastralmapCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CadastralmapId id = CadastralmapId.parse(command.cadastralmapId());
        Cadastralmap aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cadastralmap not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCadastralmapCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String cadastralmapId,
            String reason) {}
}
