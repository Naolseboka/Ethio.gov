package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.resilience.ActivateResiliencePolicyUseCase;
import et.gov.ethgov.gateway.application.resilience.CancelResiliencePolicyUseCase;
import et.gov.ethgov.gateway.application.resilience.CreateResiliencePolicyUseCase;
import et.gov.ethgov.gateway.application.resilience.GetResiliencePolicyUseCase;
import et.gov.ethgov.gateway.application.resilience.RetireResiliencePolicyUseCase;
import et.gov.ethgov.gateway.application.resilience.SuspendResiliencePolicyUseCase;
import et.gov.ethgov.gateway.application.resilience.UpdateResiliencePolicyUseCase;
import et.gov.ethgov.gateway.domain.resilience.ResiliencePolicy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/v1/resilience-policies")
public class ResiliencePolicyController {

    private final CreateResiliencePolicyUseCase create;
    private final UpdateResiliencePolicyUseCase update;
    private final ActivateResiliencePolicyUseCase activate;
    private final SuspendResiliencePolicyUseCase suspend;
    private final RetireResiliencePolicyUseCase retire;
    private final CancelResiliencePolicyUseCase cancel;
    private final GetResiliencePolicyUseCase get;

    public ResiliencePolicyController(
            CreateResiliencePolicyUseCase create,
            UpdateResiliencePolicyUseCase update,
            ActivateResiliencePolicyUseCase activate,
            SuspendResiliencePolicyUseCase suspend,
            RetireResiliencePolicyUseCase retire,
            CancelResiliencePolicyUseCase cancel,
            GetResiliencePolicyUseCase get) {
        this.create = create;
        this.update = update;
        this.activate = activate;
        this.suspend = suspend;
        this.retire = retire;
        this.cancel = cancel;
        this.get = get;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse create(@Valid @RequestBody CreateRequest request) {
        return new IdResponse(create.execute(new CreateResiliencePolicyUseCase.Command(
                        request.tenantId(),
                        request.dependencyName(),
                        request.failureRateThreshold(),
                        request.waitDurationSeconds(),
                        request.permittedCallsInHalfOpen()))
                .value());
    }

    @PutMapping("/{resiliencePolicyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String resiliencePolicyId, @Valid @RequestBody UpdateRequest request) {
        update.execute(new UpdateResiliencePolicyUseCase.Command(
                resiliencePolicyId,
                request.failureRateThreshold(),
                request.waitDurationSeconds(),
                request.permittedCallsInHalfOpen()));
    }

    @GetMapping("/lookup")
    public PolicyResponse lookup(@RequestParam String tenantId, @RequestParam String dependencyName) {
        return PolicyResponse.from(get.byDependency(tenantId, dependencyName));
    }

    @GetMapping("/{resiliencePolicyId}")
    public PolicyResponse get(@PathVariable String resiliencePolicyId) {
        return PolicyResponse.from(get.byId(resiliencePolicyId));
    }

    @PostMapping("/{resiliencePolicyId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable String resiliencePolicyId) {
        activate.execute(resiliencePolicyId);
    }

    @PostMapping("/{resiliencePolicyId}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable String resiliencePolicyId, @Valid @RequestBody ReasonRequest request) {
        suspend.execute(resiliencePolicyId, request.reason());
    }

    @PostMapping("/{resiliencePolicyId}/retire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(@PathVariable String resiliencePolicyId) {
        retire.execute(resiliencePolicyId);
    }

    @PostMapping("/{resiliencePolicyId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String resiliencePolicyId, @Valid @RequestBody ReasonRequest request) {
        cancel.execute(resiliencePolicyId, request.reason());
    }

    public record CreateRequest(
            @NotBlank String tenantId,
            @NotBlank @Size(max = 128) String dependencyName,
            @Min(1) @Max(100) int failureRateThreshold,
            @Min(1) @Max(3600) int waitDurationSeconds,
            @Min(1) @Max(100) int permittedCallsInHalfOpen) {}

    public record UpdateRequest(
            @Min(1) @Max(100) int failureRateThreshold,
            @Min(1) @Max(3600) int waitDurationSeconds,
            @Min(1) @Max(100) int permittedCallsInHalfOpen) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record IdResponse(String resiliencePolicyId) {}

    public record PolicyResponse(
            String resiliencePolicyId,
            String tenantId,
            String policyNumber,
            String dependencyName,
            int failureRateThreshold,
            int waitDurationSeconds,
            int permittedCallsInHalfOpen,
            String status) {
        static PolicyResponse from(ResiliencePolicy policy) {
            return new PolicyResponse(
                    policy.id().value(),
                    policy.tenantId().value(),
                    policy.policyNumber().value(),
                    policy.dependencyName(),
                    policy.failureRateThreshold(),
                    policy.waitDurationSeconds(),
                    policy.permittedCallsInHalfOpen(),
                    policy.status().name());
        }
    }
}
