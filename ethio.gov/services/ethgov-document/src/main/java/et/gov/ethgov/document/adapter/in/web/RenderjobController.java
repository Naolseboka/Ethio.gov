package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.renderjob.ActivateRenderjobUseCase;
import et.gov.ethgov.document.application.renderjob.CreateRenderjobUseCase;
import et.gov.ethgov.document.application.renderjob.GetRenderjobUseCase;
import et.gov.ethgov.document.application.renderjob.ListRenderjobUseCase;
import et.gov.ethgov.document.application.renderjob.SuspendRenderjobUseCase;
import et.gov.ethgov.document.application.renderjob.UpdateRenderjobUseCase;
import et.gov.ethgov.document.domain.renderjob.Renderjob;
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
@RequestMapping("/v1/document/renderjob")
public class RenderjobController {
    private final CreateRenderjobUseCase createUseCase;
    private final UpdateRenderjobUseCase updateUseCase;
    private final ActivateRenderjobUseCase activateUseCase;
    private final SuspendRenderjobUseCase suspendUseCase;
    private final GetRenderjobUseCase getUseCase;
    private final ListRenderjobUseCase listUseCase;

    public RenderjobController(
            CreateRenderjobUseCase createUseCase,
            UpdateRenderjobUseCase updateUseCase,
            ActivateRenderjobUseCase activateUseCase,
            SuspendRenderjobUseCase suspendUseCase,
            GetRenderjobUseCase getUseCase,
            ListRenderjobUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RenderjobIdResponse create(@Valid @RequestBody CreateRenderjobRequest request) {
        var id = createUseCase.execute(new CreateRenderjobUseCase.CreateRenderjobCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RenderjobIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RenderjobResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RenderjobResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RenderjobIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRenderjobRequest request) {
        var updated = updateUseCase.execute(new UpdateRenderjobUseCase.UpdateRenderjobCommand(
                null, request.title(), request.description(), null, id, null));
        return new RenderjobIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRenderjobUseCase.ActivateRenderjobCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRenderjobRequest request) {
        suspendUseCase.execute(new SuspendRenderjobUseCase.SuspendRenderjobCommand(
                null, null, null, null, id, request.reason()));
    }

    private RenderjobResponse toResponse(Renderjob aggregate) {
        return new RenderjobResponse(
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

    public record CreateRenderjobRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRenderjobRequest(String title, String description) {}

    public record SuspendRenderjobRequest(@NotBlank String reason) {}

    public record RenderjobIdResponse(String id) {}

    public record RenderjobResponse(
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
