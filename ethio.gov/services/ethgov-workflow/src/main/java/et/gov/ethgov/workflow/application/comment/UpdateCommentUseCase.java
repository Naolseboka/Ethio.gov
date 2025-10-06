package et.gov.ethgov.workflow.application.comment;

import et.gov.ethgov.workflow.domain.comment.Comment;
import et.gov.ethgov.workflow.domain.comment.CommentId;
import et.gov.ethgov.workflow.domain.comment.CommentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCommentUseCase {
    private final CommentRepository repository;
    private final Clock clock;

    public UpdateCommentUseCase(CommentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CommentId execute(UpdateCommentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CommentId id = CommentId.parse(command.commentId());
        Comment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("comment not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCommentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String commentId,
            String reason) {}
}
