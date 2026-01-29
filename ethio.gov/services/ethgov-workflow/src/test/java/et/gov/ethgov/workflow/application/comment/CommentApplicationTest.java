package et.gov.ethgov.workflow.application.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.workflow.adapter.out.persistence.InMemoryCommentRepository;
import et.gov.ethgov.workflow.domain.comment.Comment;
import et.gov.ethgov.workflow.domain.comment.CommentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommentApplicationTest {
    private InMemoryCommentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCommentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCommentUseCase(repository, clock);
        var activate = new ActivateCommentUseCase(repository, clock);
        var get = new GetCommentUseCase(repository);

        var id = create.execute(new CreateCommentUseCase.CreateCommentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCommentUseCase.ActivateCommentCommand(
                null, null, null, null, id.toString(), null));

        Comment loaded = get.execute(id.toString());
        assertEquals(CommentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
