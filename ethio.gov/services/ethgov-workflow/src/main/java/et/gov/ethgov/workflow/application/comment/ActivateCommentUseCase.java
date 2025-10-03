package et.gov.ethgov.workflow.application.comment;

import et.gov.ethgov.workflow.domain.comment.Comment;
import et.gov.ethgov.workflow.domain.comment.CommentId;
import et.gov.ethgov.workflow.domain.comment.CommentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCommentUseCase {
    private final CommentRepository repository;
    private final Clock clock;

    public ActivateCommentUseCase(CommentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CommentId execute(ActivateCommentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CommentId id = CommentId.parse(command.commentId());
        Comment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("comment not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCommentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String commentId,
            String reason) {}
}
