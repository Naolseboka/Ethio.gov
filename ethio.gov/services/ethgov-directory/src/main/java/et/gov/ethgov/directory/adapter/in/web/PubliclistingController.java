package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.publiclisting.ActivatePubliclistingUseCase;
import et.gov.ethgov.directory.application.publiclisting.CreatePubliclistingUseCase;
import et.gov.ethgov.directory.application.publiclisting.GetPubliclistingUseCase;
import et.gov.ethgov.directory.application.publiclisting.ListPubliclistingUseCase;
import et.gov.ethgov.directory.application.publiclisting.SuspendPubliclistingUseCase;
import et.gov.ethgov.directory.application.publiclisting.UpdatePubliclistingUseCase;
import et.gov.ethgov.directory.domain.publiclisting.Publiclisting;
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
@RequestMapping("/v1/directory/publiclisting")
public class PubliclistingController {
    private final CreatePubliclistingUseCase createUseCase;
    private final UpdatePubliclistingUseCase updateUseCase;
    private final ActivatePubliclistingUseCase activateUseCase;
    private final SuspendPubliclistingUseCase suspendUseCase;
    private final GetPubliclistingUseCase getUseCase;
    private final ListPubliclistingUseCase listUseCase;

    public PubliclistingController(
            CreatePubliclistingUseCase createUseCase,
            UpdatePubliclistingUseCase updateUseCase,
            ActivatePubliclistingUseCase activateUseCase,
            SuspendPubliclistingUseCase suspendUseCase,
            GetPubliclistingUseCase getUseCase,
            ListPubliclistingUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PubliclistingIdResponse create(@Valid @RequestBody CreatePubliclistingRequest request) {
        var id = createUseCase.execute(new CreatePubliclistingUseCase.CreatePubliclistingCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PubliclistingIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PubliclistingResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PubliclistingResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PubliclistingIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePubliclistingRequest request) {
        var updated = updateUseCase.execute(new UpdatePubliclistingUseCase.UpdatePubliclistingCommand(
                null, request.title(), request.description(), null, id, null));
        return new PubliclistingIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePubliclistingUseCase.ActivatePubliclistingCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPubliclistingRequest request) {
        suspendUseCase.execute(new SuspendPubliclistingUseCase.SuspendPubliclistingCommand(
                null, null, null, null, id, request.reason()));
    }

    private PubliclistingResponse toResponse(Publiclisting aggregate) {
        return new PubliclistingResponse(
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

    public record CreatePubliclistingRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePubliclistingRequest(String title, String description) {}

    public record SuspendPubliclistingRequest(@NotBlank String reason) {}

    public record PubliclistingIdResponse(String id) {}

    public record PubliclistingResponse(
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
