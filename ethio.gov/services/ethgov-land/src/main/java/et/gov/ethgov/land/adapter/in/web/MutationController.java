package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.mutation.ActivateMutationUseCase;
import et.gov.ethgov.land.application.mutation.CreateMutationUseCase;
import et.gov.ethgov.land.application.mutation.GetMutationUseCase;
import et.gov.ethgov.land.application.mutation.ListMutationUseCase;
import et.gov.ethgov.land.application.mutation.SuspendMutationUseCase;
import et.gov.ethgov.land.application.mutation.UpdateMutationUseCase;
import et.gov.ethgov.land.domain.mutation.Mutation;
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
@RequestMapping("/v1/land/mutation")
public class MutationController {
    private final CreateMutationUseCase createUseCase;
    private final UpdateMutationUseCase updateUseCase;
    private final ActivateMutationUseCase activateUseCase;
    private final SuspendMutationUseCase suspendUseCase;
    private final GetMutationUseCase getUseCase;
    private final ListMutationUseCase listUseCase;

    public MutationController(
            CreateMutationUseCase createUseCase,
            UpdateMutationUseCase updateUseCase,
            ActivateMutationUseCase activateUseCase,
            SuspendMutationUseCase suspendUseCase,
            GetMutationUseCase getUseCase,
            ListMutationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MutationIdResponse create(@Valid @RequestBody CreateMutationRequest request) {
        var id = createUseCase.execute(new CreateMutationUseCase.CreateMutationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new MutationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public MutationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<MutationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public MutationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateMutationRequest request) {
        var updated = updateUseCase.execute(new UpdateMutationUseCase.UpdateMutationCommand(
                null, request.title(), request.description(), null, id, null));
        return new MutationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateMutationUseCase.ActivateMutationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendMutationRequest request) {
        suspendUseCase.execute(new SuspendMutationUseCase.SuspendMutationCommand(
                null, null, null, null, id, request.reason()));
    }

    private MutationResponse toResponse(Mutation aggregate) {
        return new MutationResponse(
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

    public record CreateMutationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateMutationRequest(String title, String description) {}

    public record SuspendMutationRequest(@NotBlank String reason) {}

    public record MutationIdResponse(String id) {}

    public record MutationResponse(
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
