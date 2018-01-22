package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.session.ActivateSessionUseCase;
import et.gov.ethgov.identity.application.session.CreateSessionUseCase;
import et.gov.ethgov.identity.application.session.GetSessionUseCase;
import et.gov.ethgov.identity.application.session.ListSessionUseCase;
import et.gov.ethgov.identity.application.session.SuspendSessionUseCase;
import et.gov.ethgov.identity.application.session.UpdateSessionUseCase;
import et.gov.ethgov.identity.domain.session.Session;
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
@RequestMapping("/v1/identity/session")
public class SessionController {
    private final CreateSessionUseCase createUseCase;
    private final UpdateSessionUseCase updateUseCase;
    private final ActivateSessionUseCase activateUseCase;
    private final SuspendSessionUseCase suspendUseCase;
    private final GetSessionUseCase getUseCase;
    private final ListSessionUseCase listUseCase;

    public SessionController(
            CreateSessionUseCase createUseCase,
            UpdateSessionUseCase updateUseCase,
            ActivateSessionUseCase activateUseCase,
            SuspendSessionUseCase suspendUseCase,
            GetSessionUseCase getUseCase,
            ListSessionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionIdResponse create(@Valid @RequestBody CreateSessionRequest request) {
        var id = createUseCase.execute(new CreateSessionUseCase.CreateSessionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SessionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SessionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SessionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SessionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSessionRequest request) {
        var updated = updateUseCase.execute(new UpdateSessionUseCase.UpdateSessionCommand(
                null, request.title(), request.description(), null, id, null));
        return new SessionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSessionUseCase.ActivateSessionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSessionRequest request) {
        suspendUseCase.execute(new SuspendSessionUseCase.SuspendSessionCommand(
                null, null, null, null, id, request.reason()));
    }

    private SessionResponse toResponse(Session aggregate) {
        return new SessionResponse(
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

    public record CreateSessionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSessionRequest(String title, String description) {}

    public record SuspendSessionRequest(@NotBlank String reason) {}

    public record SessionIdResponse(String id) {}

    public record SessionResponse(
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
