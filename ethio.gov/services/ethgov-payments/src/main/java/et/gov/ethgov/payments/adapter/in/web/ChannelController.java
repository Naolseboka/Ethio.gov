package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.channel.ActivateChannelUseCase;
import et.gov.ethgov.payments.application.channel.CreateChannelUseCase;
import et.gov.ethgov.payments.application.channel.GetChannelUseCase;
import et.gov.ethgov.payments.application.channel.ListChannelUseCase;
import et.gov.ethgov.payments.application.channel.SuspendChannelUseCase;
import et.gov.ethgov.payments.application.channel.UpdateChannelUseCase;
import et.gov.ethgov.payments.domain.channel.Channel;
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
@RequestMapping("/v1/payments/channel")
public class ChannelController {
    private final CreateChannelUseCase createUseCase;
    private final UpdateChannelUseCase updateUseCase;
    private final ActivateChannelUseCase activateUseCase;
    private final SuspendChannelUseCase suspendUseCase;
    private final GetChannelUseCase getUseCase;
    private final ListChannelUseCase listUseCase;

    public ChannelController(
            CreateChannelUseCase createUseCase,
            UpdateChannelUseCase updateUseCase,
            ActivateChannelUseCase activateUseCase,
            SuspendChannelUseCase suspendUseCase,
            GetChannelUseCase getUseCase,
            ListChannelUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelIdResponse create(@Valid @RequestBody CreateChannelRequest request) {
        var id = createUseCase.execute(new CreateChannelUseCase.CreateChannelCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ChannelIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ChannelResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ChannelResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ChannelIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateChannelRequest request) {
        var updated = updateUseCase.execute(new UpdateChannelUseCase.UpdateChannelCommand(
                null, request.title(), request.description(), null, id, null));
        return new ChannelIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateChannelUseCase.ActivateChannelCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendChannelRequest request) {
        suspendUseCase.execute(new SuspendChannelUseCase.SuspendChannelCommand(
                null, null, null, null, id, request.reason()));
    }

    private ChannelResponse toResponse(Channel aggregate) {
        return new ChannelResponse(
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

    public record CreateChannelRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateChannelRequest(String title, String description) {}

    public record SuspendChannelRequest(@NotBlank String reason) {}

    public record ChannelIdResponse(String id) {}

    public record ChannelResponse(
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
