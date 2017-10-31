package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.openinghours.ActivateOpeninghoursUseCase;
import et.gov.ethgov.directory.application.openinghours.CreateOpeninghoursUseCase;
import et.gov.ethgov.directory.application.openinghours.GetOpeninghoursUseCase;
import et.gov.ethgov.directory.application.openinghours.ListOpeninghoursUseCase;
import et.gov.ethgov.directory.application.openinghours.SuspendOpeninghoursUseCase;
import et.gov.ethgov.directory.application.openinghours.UpdateOpeninghoursUseCase;
import et.gov.ethgov.directory.domain.openinghours.Openinghours;
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
@RequestMapping("/v1/directory/openinghours")
public class OpeninghoursController {
    private final CreateOpeninghoursUseCase createUseCase;
    private final UpdateOpeninghoursUseCase updateUseCase;
    private final ActivateOpeninghoursUseCase activateUseCase;
    private final SuspendOpeninghoursUseCase suspendUseCase;
    private final GetOpeninghoursUseCase getUseCase;
    private final ListOpeninghoursUseCase listUseCase;

    public OpeninghoursController(
            CreateOpeninghoursUseCase createUseCase,
            UpdateOpeninghoursUseCase updateUseCase,
            ActivateOpeninghoursUseCase activateUseCase,
            SuspendOpeninghoursUseCase suspendUseCase,
            GetOpeninghoursUseCase getUseCase,
            ListOpeninghoursUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OpeninghoursIdResponse create(@Valid @RequestBody CreateOpeninghoursRequest request) {
        var id = createUseCase.execute(new CreateOpeninghoursUseCase.CreateOpeninghoursCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new OpeninghoursIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public OpeninghoursResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<OpeninghoursResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public OpeninghoursIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateOpeninghoursRequest request) {
        var updated = updateUseCase.execute(new UpdateOpeninghoursUseCase.UpdateOpeninghoursCommand(
                null, request.title(), request.description(), null, id, null));
        return new OpeninghoursIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateOpeninghoursUseCase.ActivateOpeninghoursCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendOpeninghoursRequest request) {
        suspendUseCase.execute(new SuspendOpeninghoursUseCase.SuspendOpeninghoursCommand(
                null, null, null, null, id, request.reason()));
    }

    private OpeninghoursResponse toResponse(Openinghours aggregate) {
        return new OpeninghoursResponse(
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

    public record CreateOpeninghoursRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateOpeninghoursRequest(String title, String description) {}

    public record SuspendOpeninghoursRequest(@NotBlank String reason) {}

    public record OpeninghoursIdResponse(String id) {}

    public record OpeninghoursResponse(
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
