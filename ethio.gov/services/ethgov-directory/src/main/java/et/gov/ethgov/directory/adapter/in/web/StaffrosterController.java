package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.staffroster.ActivateStaffrosterUseCase;
import et.gov.ethgov.directory.application.staffroster.CreateStaffrosterUseCase;
import et.gov.ethgov.directory.application.staffroster.GetStaffrosterUseCase;
import et.gov.ethgov.directory.application.staffroster.ListStaffrosterUseCase;
import et.gov.ethgov.directory.application.staffroster.SuspendStaffrosterUseCase;
import et.gov.ethgov.directory.application.staffroster.UpdateStaffrosterUseCase;
import et.gov.ethgov.directory.domain.staffroster.Staffroster;
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
@RequestMapping("/v1/directory/staffroster")
public class StaffrosterController {
    private final CreateStaffrosterUseCase createUseCase;
    private final UpdateStaffrosterUseCase updateUseCase;
    private final ActivateStaffrosterUseCase activateUseCase;
    private final SuspendStaffrosterUseCase suspendUseCase;
    private final GetStaffrosterUseCase getUseCase;
    private final ListStaffrosterUseCase listUseCase;

    public StaffrosterController(
            CreateStaffrosterUseCase createUseCase,
            UpdateStaffrosterUseCase updateUseCase,
            ActivateStaffrosterUseCase activateUseCase,
            SuspendStaffrosterUseCase suspendUseCase,
            GetStaffrosterUseCase getUseCase,
            ListStaffrosterUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StaffrosterIdResponse create(@Valid @RequestBody CreateStaffrosterRequest request) {
        var id = createUseCase.execute(new CreateStaffrosterUseCase.CreateStaffrosterCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new StaffrosterIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public StaffrosterResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<StaffrosterResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public StaffrosterIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateStaffrosterRequest request) {
        var updated = updateUseCase.execute(new UpdateStaffrosterUseCase.UpdateStaffrosterCommand(
                null, request.title(), request.description(), null, id, null));
        return new StaffrosterIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateStaffrosterUseCase.ActivateStaffrosterCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendStaffrosterRequest request) {
        suspendUseCase.execute(new SuspendStaffrosterUseCase.SuspendStaffrosterCommand(
                null, null, null, null, id, request.reason()));
    }

    private StaffrosterResponse toResponse(Staffroster aggregate) {
        return new StaffrosterResponse(
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

    public record CreateStaffrosterRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateStaffrosterRequest(String title, String description) {}

    public record SuspendStaffrosterRequest(@NotBlank String reason) {}

    public record StaffrosterIdResponse(String id) {}

    public record StaffrosterResponse(
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
