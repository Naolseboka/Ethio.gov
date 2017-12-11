package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.ratelimit.ActivateRateLimitPolicyUseCase;
import et.gov.ethgov.gateway.application.ratelimit.CancelRateLimitPolicyUseCase;
import et.gov.ethgov.gateway.application.ratelimit.CreateRateLimitPolicyUseCase;
import et.gov.ethgov.gateway.application.ratelimit.EvaluateRateLimitUseCase;
import et.gov.ethgov.gateway.application.ratelimit.GetRateLimitPolicyUseCase;
import et.gov.ethgov.gateway.application.ratelimit.RetireRateLimitPolicyUseCase;
import et.gov.ethgov.gateway.application.ratelimit.SuspendRateLimitPolicyUseCase;
import et.gov.ethgov.gateway.application.ratelimit.UpdateRateLimitPolicyUseCase;
import et.gov.ethgov.gateway.domain.ratelimit.RateLimitPolicy;
import et.gov.ethgov.platform.commons.error.DomainException;
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
@RequestMapping("/v1/rate-limits")
public class RateLimitPolicyController {

    private final CreateRateLimitPolicyUseCase create;
    private final UpdateRateLimitPolicyUseCase update;
    private final ActivateRateLimitPolicyUseCase activate;
    private final SuspendRateLimitPolicyUseCase suspend;
    private final RetireRateLimitPolicyUseCase retire;
    private final CancelRateLimitPolicyUseCase cancel;
    private final GetRateLimitPolicyUseCase get;
    private final EvaluateRateLimitUseCase evaluate;

    public RateLimitPolicyController(
            CreateRateLimitPolicyUseCase create,
            UpdateRateLimitPolicyUseCase update,
            ActivateRateLimitPolicyUseCase activate,
            SuspendRateLimitPolicyUseCase suspend,
            RetireRateLimitPolicyUseCase retire,
            CancelRateLimitPolicyUseCase cancel,
            GetRateLimitPolicyUseCase get,
            EvaluateRateLimitUseCase evaluate) {
        this.create = create;
        this.update = update;
        this.activate = activate;
        this.suspend = suspend;
        this.retire = retire;
        this.cancel = cancel;
        this.get = get;
        this.evaluate = evaluate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse create(@Valid @RequestBody CreateRequest request) {
        return new IdResponse(create.execute(new CreateRateLimitPolicyUseCase.Command(
                        request.tenantId(),
                        request.name(),
                        request.subjectType(),
                        request.subjectKey(),
                        request.limitPerWindow(),
                        request.windowSeconds()))
                .value());
    }

    @PutMapping("/{rateLimitPolicyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String rateLimitPolicyId, @Valid @RequestBody UpdateRequest request) {
        update.execute(new UpdateRateLimitPolicyUseCase.Command(
                rateLimitPolicyId, request.name(), request.limitPerWindow(), request.windowSeconds()));
    }

    @GetMapping("/lookup")
    public PolicyResponse lookup(@RequestParam String tenantId, @RequestParam String policyNumber) {
        return PolicyResponse.from(get.byNumber(tenantId, policyNumber));
    }

    @GetMapping("/{rateLimitPolicyId}")
    public PolicyResponse get(@PathVariable String rateLimitPolicyId) {
        return PolicyResponse.from(get.byId(rateLimitPolicyId));
    }

    @PostMapping("/{rateLimitPolicyId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable String rateLimitPolicyId) {
        activate.execute(rateLimitPolicyId);
    }

    @PostMapping("/{rateLimitPolicyId}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable String rateLimitPolicyId, @Valid @RequestBody ReasonRequest request) {
        suspend.execute(rateLimitPolicyId, request.reason());
    }

    @PostMapping("/{rateLimitPolicyId}/retire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(@PathVariable String rateLimitPolicyId) {
        retire.execute(rateLimitPolicyId);
    }

    @PostMapping("/{rateLimitPolicyId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String rateLimitPolicyId, @Valid @RequestBody ReasonRequest request) {
        cancel.execute(rateLimitPolicyId, request.reason());
    }

    @PostMapping("/evaluate")
    public EvaluateResponse evaluate(@Valid @RequestBody EvaluateRequest request) {
        EvaluateRateLimitUseCase.Result result = evaluate.execute(new EvaluateRateLimitUseCase.Command(
                request.tenantId(), request.subjectType(), request.subjectKey(), request.actorId()));
        if (!result.allowed()) {
            throw new DomainException(
                    "RATE_LIMIT_EXCEEDED",
                    "Rate Limit Exceeded",
                    "Request exceeded limit of %d per %d seconds"
                            .formatted(result.limitPerWindow(), result.windowSeconds()),
                    429);
        }
        return new EvaluateResponse(
                result.rateLimitPolicyId(),
                result.allowed(),
                result.hits(),
                result.limitPerWindow(),
                result.windowSeconds(),
                result.windowStart().toString());
    }

    public record CreateRequest(
            @NotBlank String tenantId,
            @NotBlank @Size(max = 200) String name,
            @NotBlank String subjectType,
            @NotBlank String subjectKey,
            @Min(1) @Max(1_000_000) int limitPerWindow,
            @Min(1) @Max(86_400) int windowSeconds) {}

    public record UpdateRequest(
            @NotBlank @Size(max = 200) String name,
            @Min(1) @Max(1_000_000) int limitPerWindow,
            @Min(1) @Max(86_400) int windowSeconds) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record EvaluateRequest(
            @NotBlank String tenantId,
            @NotBlank String subjectType,
            @NotBlank String subjectKey,
            String actorId) {}

    public record IdResponse(String rateLimitPolicyId) {}

    public record EvaluateResponse(
            String rateLimitPolicyId,
            boolean allowed,
            long hits,
            int limitPerWindow,
            int windowSeconds,
            String windowStart) {}

    public record PolicyResponse(
            String rateLimitPolicyId,
            String tenantId,
            String policyNumber,
            String name,
            String subjectType,
            String subjectKey,
            int limitPerWindow,
            int windowSeconds,
            String status) {
        static PolicyResponse from(RateLimitPolicy policy) {
            return new PolicyResponse(
                    policy.id().value(),
                    policy.tenantId().value(),
                    policy.policyNumber().value(),
                    policy.name(),
                    policy.subjectType().name(),
                    policy.subjectKey(),
                    policy.limitPerWindow(),
                    policy.windowSeconds(),
                    policy.status().name());
        }
    }
}
