package et.gov.ethgov.search.application.ranking;

import et.gov.ethgov.search.domain.ranking.Ranking;
import et.gov.ethgov.search.domain.ranking.RankingId;
import et.gov.ethgov.search.domain.ranking.RankingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRankingUseCase {
    private final RankingRepository repository;
    private final Clock clock;

    public CreateRankingUseCase(RankingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RankingId execute(CreateRankingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Ranking aggregate = Ranking.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRankingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String rankingId,
            String reason) {}
}
