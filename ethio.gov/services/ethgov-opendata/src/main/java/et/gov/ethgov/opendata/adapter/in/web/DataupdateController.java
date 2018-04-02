package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.dataupdate.ActivateDataupdateUseCase;
import et.gov.ethgov.opendata.application.dataupdate.CreateDataupdateUseCase;
import et.gov.ethgov.opendata.application.dataupdate.GetDataupdateUseCase;
import et.gov.ethgov.opendata.application.dataupdate.ListDataupdateUseCase;
import et.gov.ethgov.opendata.application.dataupdate.SuspendDataupdateUseCase;
import et.gov.ethgov.opendata.application.dataupdate.UpdateDataupdateUseCase;
import et.gov.ethgov.opendata.domain.dataupdate.Dataupdate;
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
@RequestMapping("/v1/opendata/dataupdate")
public class DataupdateController {
    private final CreateDataupdateUseCase createUseCase;
    private final UpdateDataupdateUseCase updateUseCase;
    private final ActivateDataupdateUseCase activateUseCase;
    private final SuspendDataupdateUseCase suspendUseCase;
    private final GetDataupdateUseCase getUseCase;
    private final ListDataupdateUseCase listUseCase;

    public DataupdateController(
            CreateDataupdateUseCase createUseCase,
            UpdateDataupdateUseCase updateUseCase,
            ActivateDataupdateUseCase activateUseCase,
            SuspendDataupdateUseCase suspendUseCase,
            GetDataupdateUseCase getUseCase,
            ListDataupdateUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DataupdateIdResponse create(@Valid @RequestBody CreateDataupdateRequest request) {
        var id = createUseCase.execute(new CreateDataupdateUseCase.CreateDataupdateCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DataupdateIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DataupdateResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DataupdateResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DataupdateIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDataupdateRequest request) {
        var updated = updateUseCase.execute(new UpdateDataupdateUseCase.UpdateDataupdateCommand(
                null, request.title(), request.description(), null, id, null));
        return new DataupdateIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDataupdateUseCase.ActivateDataupdateCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDataupdateRequest request) {
        suspendUseCase.execute(new SuspendDataupdateUseCase.SuspendDataupdateCommand(
                null, null, null, null, id, request.reason()));
    }

    private DataupdateResponse toResponse(Dataupdate aggregate) {
        return new DataupdateResponse(
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

    public record CreateDataupdateRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDataupdateRequest(String title, String description) {}

    public record SuspendDataupdateRequest(@NotBlank String reason) {}

    public record DataupdateIdResponse(String id) {}

    public record DataupdateResponse(
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
