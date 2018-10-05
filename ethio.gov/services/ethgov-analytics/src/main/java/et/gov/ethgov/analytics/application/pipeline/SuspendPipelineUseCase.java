package et.gov.ethgov.analytics.application.pipeline;

import et.gov.ethgov.analytics.domain.pipeline.Pipeline;
import et.gov.ethgov.analytics.domain.pipeline.PipelineId;
import et.gov.ethgov.analytics.domain.pipeline.PipelineRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPipelineUseCase {
    private final PipelineRepository repository;
    private final Clock clock;

    public SuspendPipelineUseCase(PipelineRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PipelineId execute(SuspendPipelineCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PipelineId id = PipelineId.parse(command.pipelineId());
        Pipeline aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("pipeline not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPipelineCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String pipelineId,
            String reason) {}
}
