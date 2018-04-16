package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.membership.ActivateMembershipUseCase;
import et.gov.ethgov.organization.application.membership.CreateMembershipUseCase;
import et.gov.ethgov.organization.application.membership.GetMembershipUseCase;
import et.gov.ethgov.organization.application.membership.ListMembershipUseCase;
import et.gov.ethgov.organization.application.membership.SuspendMembershipUseCase;
import et.gov.ethgov.organization.application.membership.UpdateMembershipUseCase;
import et.gov.ethgov.organization.domain.membership.Membership;
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
@RequestMapping("/v1/organization/membership")
public class MembershipController {
    private final CreateMembershipUseCase createUseCase;
    private final UpdateMembershipUseCase updateUseCase;
    private final ActivateMembershipUseCase activateUseCase;
    private final SuspendMembershipUseCase suspendUseCase;
    private final GetMembershipUseCase getUseCase;
    private final ListMembershipUseCase listUseCase;

    public MembershipController(
            CreateMembershipUseCase createUseCase,
            UpdateMembershipUseCase updateUseCase,
            ActivateMembershipUseCase activateUseCase,
            SuspendMembershipUseCase suspendUseCase,
            GetMembershipUseCase getUseCase,
            ListMembershipUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipIdResponse create(@Valid @RequestBody CreateMembershipRequest request) {
        var id = createUseCase.execute(new CreateMembershipUseCase.CreateMembershipCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new MembershipIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public MembershipResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<MembershipResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public MembershipIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateMembershipRequest request) {
        var updated = updateUseCase.execute(new UpdateMembershipUseCase.UpdateMembershipCommand(
                null, request.title(), request.description(), null, id, null));
        return new MembershipIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateMembershipUseCase.ActivateMembershipCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendMembershipRequest request) {
        suspendUseCase.execute(new SuspendMembershipUseCase.SuspendMembershipCommand(
                null, null, null, null, id, request.reason()));
    }

    private MembershipResponse toResponse(Membership aggregate) {
        return new MembershipResponse(
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

    public record CreateMembershipRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateMembershipRequest(String title, String description) {}

    public record SuspendMembershipRequest(@NotBlank String reason) {}

    public record MembershipIdResponse(String id) {}

    public record MembershipResponse(
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
