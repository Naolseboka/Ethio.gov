package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.route.ActivateRouteUseCase;
import et.gov.ethgov.gateway.application.route.CreateRouteUseCase;
import et.gov.ethgov.gateway.application.route.GetRouteUseCase;
import et.gov.ethgov.gateway.application.route.ListRouteUseCase;
import et.gov.ethgov.gateway.application.route.SuspendRouteUseCase;
import et.gov.ethgov.gateway.application.route.UpdateRouteUseCase;
import et.gov.ethgov.gateway.domain.route.Route;
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
@RequestMapping("/v1/gateway/route")
public class RouteController {
    private final CreateRouteUseCase createUseCase;
    private final UpdateRouteUseCase updateUseCase;
    private final ActivateRouteUseCase activateUseCase;
    private final SuspendRouteUseCase suspendUseCase;
    private final GetRouteUseCase getUseCase;
    private final ListRouteUseCase listUseCase;

    public RouteController(
            CreateRouteUseCase createUseCase,
            UpdateRouteUseCase updateUseCase,
            ActivateRouteUseCase activateUseCase,
            SuspendRouteUseCase suspendUseCase,
            GetRouteUseCase getUseCase,
            ListRouteUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RouteIdResponse create(@Valid @RequestBody CreateRouteRequest request) {
        var id = createUseCase.execute(new CreateRouteUseCase.CreateRouteCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RouteIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RouteResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RouteResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RouteIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRouteRequest request) {
        var updated = updateUseCase.execute(new UpdateRouteUseCase.UpdateRouteCommand(
                null, request.title(), request.description(), null, id, null));
        return new RouteIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRouteUseCase.ActivateRouteCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRouteRequest request) {
        suspendUseCase.execute(new SuspendRouteUseCase.SuspendRouteCommand(
                null, null, null, null, id, request.reason()));
    }

    private RouteResponse toResponse(Route aggregate) {
        return new RouteResponse(
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

    public record CreateRouteRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRouteRequest(String title, String description) {}

    public record SuspendRouteRequest(@NotBlank String reason) {}

    public record RouteIdResponse(String id) {}

    public record RouteResponse(
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
