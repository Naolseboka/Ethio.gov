package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.apikey.ActivateApikeyUseCase;
import et.gov.ethgov.identity.application.apikey.CreateApikeyUseCase;
import et.gov.ethgov.identity.application.apikey.GetApikeyUseCase;
import et.gov.ethgov.identity.application.apikey.ListApikeyUseCase;
import et.gov.ethgov.identity.application.apikey.SuspendApikeyUseCase;
import et.gov.ethgov.identity.application.apikey.UpdateApikeyUseCase;
import et.gov.ethgov.identity.domain.apikey.Apikey;
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
@RequestMapping("/v1/identity/apikey")
public class ApikeyController {
    private final CreateApikeyUseCase createUseCase;
    private final UpdateApikeyUseCase updateUseCase;
    private final ActivateApikeyUseCase activateUseCase;
    private final SuspendApikeyUseCase suspendUseCase;
    private final GetApikeyUseCase getUseCase;
    private final ListApikeyUseCase listUseCase;

    public ApikeyController(
            CreateApikeyUseCase createUseCase,
            UpdateApikeyUseCase updateUseCase,
            ActivateApikeyUseCase activateUseCase,
            SuspendApikeyUseCase suspendUseCase,
            GetApikeyUseCase getUseCase,
            ListApikeyUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApikeyIdResponse create(@Valid @RequestBody CreateApikeyRequest request) {
        var id = createUseCase.execute(new CreateApikeyUseCase.CreateApikeyCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ApikeyIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ApikeyResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ApikeyResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ApikeyIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateApikeyRequest request) {
        var updated = updateUseCase.execute(new UpdateApikeyUseCase.UpdateApikeyCommand(
                null, request.title(), request.description(), null, id, null));
        return new ApikeyIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateApikeyUseCase.ActivateApikeyCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendApikeyRequest request) {
        suspendUseCase.execute(new SuspendApikeyUseCase.SuspendApikeyCommand(
                null, null, null, null, id, request.reason()));
    }

    private ApikeyResponse toResponse(Apikey aggregate) {
        return new ApikeyResponse(
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

    public record CreateApikeyRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateApikeyRequest(String title, String description) {}

    public record SuspendApikeyRequest(@NotBlank String reason) {}

    public record ApikeyIdResponse(String id) {}

    public record ApikeyResponse(
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
