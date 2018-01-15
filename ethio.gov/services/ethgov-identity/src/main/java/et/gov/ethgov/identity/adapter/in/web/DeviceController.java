package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.device.ActivateDeviceUseCase;
import et.gov.ethgov.identity.application.device.CreateDeviceUseCase;
import et.gov.ethgov.identity.application.device.GetDeviceUseCase;
import et.gov.ethgov.identity.application.device.ListDeviceUseCase;
import et.gov.ethgov.identity.application.device.SuspendDeviceUseCase;
import et.gov.ethgov.identity.application.device.UpdateDeviceUseCase;
import et.gov.ethgov.identity.domain.device.Device;
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
@RequestMapping("/v1/identity/device")
public class DeviceController {
    private final CreateDeviceUseCase createUseCase;
    private final UpdateDeviceUseCase updateUseCase;
    private final ActivateDeviceUseCase activateUseCase;
    private final SuspendDeviceUseCase suspendUseCase;
    private final GetDeviceUseCase getUseCase;
    private final ListDeviceUseCase listUseCase;

    public DeviceController(
            CreateDeviceUseCase createUseCase,
            UpdateDeviceUseCase updateUseCase,
            ActivateDeviceUseCase activateUseCase,
            SuspendDeviceUseCase suspendUseCase,
            GetDeviceUseCase getUseCase,
            ListDeviceUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceIdResponse create(@Valid @RequestBody CreateDeviceRequest request) {
        var id = createUseCase.execute(new CreateDeviceUseCase.CreateDeviceCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DeviceIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DeviceResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DeviceResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DeviceIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDeviceRequest request) {
        var updated = updateUseCase.execute(new UpdateDeviceUseCase.UpdateDeviceCommand(
                null, request.title(), request.description(), null, id, null));
        return new DeviceIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDeviceUseCase.ActivateDeviceCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDeviceRequest request) {
        suspendUseCase.execute(new SuspendDeviceUseCase.SuspendDeviceCommand(
                null, null, null, null, id, request.reason()));
    }

    private DeviceResponse toResponse(Device aggregate) {
        return new DeviceResponse(
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

    public record CreateDeviceRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDeviceRequest(String title, String description) {}

    public record SuspendDeviceRequest(@NotBlank String reason) {}

    public record DeviceIdResponse(String id) {}

    public record DeviceResponse(
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
