package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.regionnode.ActivateRegionnodeUseCase;
import et.gov.ethgov.directory.application.regionnode.CreateRegionnodeUseCase;
import et.gov.ethgov.directory.application.regionnode.GetRegionnodeUseCase;
import et.gov.ethgov.directory.application.regionnode.ListRegionnodeUseCase;
import et.gov.ethgov.directory.application.regionnode.SuspendRegionnodeUseCase;
import et.gov.ethgov.directory.application.regionnode.UpdateRegionnodeUseCase;
import et.gov.ethgov.directory.domain.regionnode.Regionnode;
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
@RequestMapping("/v1/directory/regionnode")
public class RegionnodeController {
    private final CreateRegionnodeUseCase createUseCase;
    private final UpdateRegionnodeUseCase updateUseCase;
    private final ActivateRegionnodeUseCase activateUseCase;
    private final SuspendRegionnodeUseCase suspendUseCase;
    private final GetRegionnodeUseCase getUseCase;
    private final ListRegionnodeUseCase listUseCase;

    public RegionnodeController(
            CreateRegionnodeUseCase createUseCase,
            UpdateRegionnodeUseCase updateUseCase,
            ActivateRegionnodeUseCase activateUseCase,
            SuspendRegionnodeUseCase suspendUseCase,
            GetRegionnodeUseCase getUseCase,
            ListRegionnodeUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegionnodeIdResponse create(@Valid @RequestBody CreateRegionnodeRequest request) {
        var id = createUseCase.execute(new CreateRegionnodeUseCase.CreateRegionnodeCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RegionnodeIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RegionnodeResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RegionnodeResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RegionnodeIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRegionnodeRequest request) {
        var updated = updateUseCase.execute(new UpdateRegionnodeUseCase.UpdateRegionnodeCommand(
                null, request.title(), request.description(), null, id, null));
        return new RegionnodeIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRegionnodeUseCase.ActivateRegionnodeCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRegionnodeRequest request) {
        suspendUseCase.execute(new SuspendRegionnodeUseCase.SuspendRegionnodeCommand(
                null, null, null, null, id, request.reason()));
    }

    private RegionnodeResponse toResponse(Regionnode aggregate) {
        return new RegionnodeResponse(
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

    public record CreateRegionnodeRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRegionnodeRequest(String title, String description) {}

    public record SuspendRegionnodeRequest(@NotBlank String reason) {}

    public record RegionnodeIdResponse(String id) {}

    public record RegionnodeResponse(
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
