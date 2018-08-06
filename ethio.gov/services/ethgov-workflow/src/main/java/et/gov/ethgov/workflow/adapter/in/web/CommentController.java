package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.comment.ActivateCommentUseCase;
import et.gov.ethgov.workflow.application.comment.CreateCommentUseCase;
import et.gov.ethgov.workflow.application.comment.GetCommentUseCase;
import et.gov.ethgov.workflow.application.comment.ListCommentUseCase;
import et.gov.ethgov.workflow.application.comment.SuspendCommentUseCase;
import et.gov.ethgov.workflow.application.comment.UpdateCommentUseCase;
import et.gov.ethgov.workflow.domain.comment.Comment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/workflow/comment")
public class CommentController {
    private final CreateCommentUseCase createUseCase;
    private final UpdateCommentUseCase updateUseCase;
    private final ActivateCommentUseCase activateUseCase;
    private final SuspendCommentUseCase suspendUseCase;
    private final GetCommentUseCase getUseCase;
    private final ListCommentUseCase listUseCase;

    public CommentController(
            CreateCommentUseCase createUseCase,
            UpdateCommentUseCase updateUseCase,
            ActivateCommentUseCase activateUseCase,
            SuspendCommentUseCase suspendUseCase,
            GetCommentUseCase getUseCase,
            ListCommentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentIdResponse create(@Valid @RequestBody CreateCommentRequest request) {
        var id = createUseCase.execute(new CreateCommentUseCase.CreateCommentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CommentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CommentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CommentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CommentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCommentRequest request) {
        var updated = updateUseCase.execute(new UpdateCommentUseCase.UpdateCommentCommand(
                null, request.title(), request.description(), null, id, null));
        return new CommentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCommentUseCase.ActivateCommentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCommentRequest request) {
        suspendUseCase.execute(new SuspendCommentUseCase.SuspendCommentCommand(
                null, null, null, null, id, request.reason()));
    }

    private CommentResponse toResponse(Comment aggregate) {
        return new CommentResponse(
                aggregate.id().toString(),
                aggregate.referenceCode(),
                aggregate.title(),
                aggregate.description(),
                aggregate.tenantId(),
                aggregate.status().name(),
                aggregate.createdAt().toString(),
                aggregate.updatedAt().toString(),
                aggregate.version());
    }

    public record CreateCommentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCommentRequest(String title, String description) {}

    public record SuspendCommentRequest(@NotBlank String reason) {}

    public record CommentIdResponse(String id) {}

    public record CommentResponse(
            String id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            String createdAt,
            String updatedAt,
            long version) {}
}
