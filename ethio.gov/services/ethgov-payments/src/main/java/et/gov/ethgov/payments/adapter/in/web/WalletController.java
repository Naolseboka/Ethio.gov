package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.wallet.ActivateWalletUseCase;
import et.gov.ethgov.payments.application.wallet.CreateWalletUseCase;
import et.gov.ethgov.payments.application.wallet.GetWalletUseCase;
import et.gov.ethgov.payments.application.wallet.ListWalletUseCase;
import et.gov.ethgov.payments.application.wallet.SuspendWalletUseCase;
import et.gov.ethgov.payments.application.wallet.UpdateWalletUseCase;
import et.gov.ethgov.payments.domain.wallet.Wallet;
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
@RequestMapping("/v1/payments/wallet")
public class WalletController {
    private final CreateWalletUseCase createUseCase;
    private final UpdateWalletUseCase updateUseCase;
    private final ActivateWalletUseCase activateUseCase;
    private final SuspendWalletUseCase suspendUseCase;
    private final GetWalletUseCase getUseCase;
    private final ListWalletUseCase listUseCase;

    public WalletController(
            CreateWalletUseCase createUseCase,
            UpdateWalletUseCase updateUseCase,
            ActivateWalletUseCase activateUseCase,
            SuspendWalletUseCase suspendUseCase,
            GetWalletUseCase getUseCase,
            ListWalletUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletIdResponse create(@Valid @RequestBody CreateWalletRequest request) {
        var id = createUseCase.execute(new CreateWalletUseCase.CreateWalletCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new WalletIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public WalletResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<WalletResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public WalletIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateWalletRequest request) {
        var updated = updateUseCase.execute(new UpdateWalletUseCase.UpdateWalletCommand(
                null, request.title(), request.description(), null, id, null));
        return new WalletIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateWalletUseCase.ActivateWalletCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendWalletRequest request) {
        suspendUseCase.execute(new SuspendWalletUseCase.SuspendWalletCommand(
                null, null, null, null, id, request.reason()));
    }

    private WalletResponse toResponse(Wallet aggregate) {
        return new WalletResponse(
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

    public record CreateWalletRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateWalletRequest(String title, String description) {}

    public record SuspendWalletRequest(@NotBlank String reason) {}

    public record WalletIdResponse(String id) {}

    public record WalletResponse(
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
