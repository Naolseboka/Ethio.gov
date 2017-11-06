package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.ward.ActivateWardUseCase;
import et.gov.ethgov.directory.application.ward.CreateWardUseCase;
import et.gov.ethgov.directory.application.ward.GetWardUseCase;
import et.gov.ethgov.directory.application.ward.ListWardUseCase;
import et.gov.ethgov.directory.application.ward.SuspendWardUseCase;
import et.gov.ethgov.directory.application.ward.UpdateWardUseCase;
import et.gov.ethgov.directory.domain.ward.Ward;
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
@RequestMapping("/v1/directory/ward")
public class WardController {
    private final CreateWardUseCase createUseCase;
    private final UpdateWardUseCase updateUseCase;
    private final ActivateWardUseCase activateUseCase;
    private final SuspendWardUseCase suspendUseCase;
    private final GetWardUseCase getUseCase;
    private final ListWardUseCase listUseCase;

    public WardController(
            CreateWardUseCase createUseCase,
            UpdateWardUseCase updateUseCase,
            ActivateWardUseCase activateUseCase,
            SuspendWardUseCase suspendUseCase,
            GetWardUseCase getUseCase,
            ListWardUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WardIdResponse create(@Valid @RequestBody CreateWardRequest request) {
        var id = createUseCase.execute(new CreateWardUseCase.CreateWardCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new WardIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public WardResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<WardResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public WardIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateWardRequest request) {
        var updated = updateUseCase.execute(new UpdateWardUseCase.UpdateWardCommand(
                null, request.title(), request.description(), null, id, null));
        return new WardIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateWardUseCase.ActivateWardCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendWardRequest request) {
        suspendUseCase.execute(new SuspendWardUseCase.SuspendWardCommand(
                null, null, null, null, id, request.reason()));
    }

    private WardResponse toResponse(Ward aggregate) {
        return new WardResponse(
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

    public record CreateWardRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateWardRequest(String title, String description) {}

    public record SuspendWardRequest(@NotBlank String reason) {}

    public record WardIdResponse(String id) {}

    public record WardResponse(
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
