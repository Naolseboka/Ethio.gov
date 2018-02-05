package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.parcel.AttachParcelDocumentUseCase;
import et.gov.ethgov.land.application.parcel.CancelParcelUseCase;
import et.gov.ethgov.land.application.parcel.ClearEncumbranceUseCase;
import et.gov.ethgov.land.application.parcel.EncumberParcelUseCase;
import et.gov.ethgov.land.application.parcel.GetParcelUseCase;
import et.gov.ethgov.land.application.parcel.LinkParcelCaseUseCase;
import et.gov.ethgov.land.application.parcel.RegisterParcelUseCase;
import et.gov.ethgov.land.application.parcel.RetireParcelUseCase;
import et.gov.ethgov.land.application.parcel.TransferParcelUseCase;
import et.gov.ethgov.land.application.parcel.UpdateParcelUseCase;
import et.gov.ethgov.land.domain.parcel.LandParcel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
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
@RequestMapping("/v1/parcels")
public class LandController {

    private final RegisterParcelUseCase registerParcel;
    private final UpdateParcelUseCase updateParcel;
    private final TransferParcelUseCase transferParcel;
    private final EncumberParcelUseCase encumberParcel;
    private final ClearEncumbranceUseCase clearEncumbrance;
    private final AttachParcelDocumentUseCase attachDocument;
    private final LinkParcelCaseUseCase linkCase;
    private final RetireParcelUseCase retireParcel;
    private final CancelParcelUseCase cancelParcel;
    private final GetParcelUseCase getParcel;

    public LandController(
            RegisterParcelUseCase registerParcel,
            UpdateParcelUseCase updateParcel,
            TransferParcelUseCase transferParcel,
            EncumberParcelUseCase encumberParcel,
            ClearEncumbranceUseCase clearEncumbrance,
            AttachParcelDocumentUseCase attachDocument,
            LinkParcelCaseUseCase linkCase,
            RetireParcelUseCase retireParcel,
            CancelParcelUseCase cancelParcel,
            GetParcelUseCase getParcel) {
        this.registerParcel = registerParcel;
        this.updateParcel = updateParcel;
        this.transferParcel = transferParcel;
        this.encumberParcel = encumberParcel;
        this.clearEncumbrance = clearEncumbrance;
        this.attachDocument = attachDocument;
        this.linkCase = linkCase;
        this.retireParcel = retireParcel;
        this.cancelParcel = cancelParcel;
        this.getParcel = getParcel;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParcelIdResponse register(@Valid @RequestBody RegisterParcelRequest request) {
        return new ParcelIdResponse(registerParcel.execute(new RegisterParcelUseCase.Command(
                        request.tenantId(),
                        request.region(),
                        request.zone(),
                        request.woreda(),
                        request.kebele(),
                        request.locality(),
                        request.landUse(),
                        request.tenureType(),
                        request.areaSquareMeters(),
                        request.citizenId(),
                        request.organizationId(),
                        request.titleDeedNumber(),
                        request.leaseReference(),
                        request.leaseExpiresAt(),
                        request.linkedCaseId()))
                .value());
    }

    @GetMapping("/lookup")
    public ParcelResponse lookup(@RequestParam String tenantId, @RequestParam String parcelNumber) {
        return ParcelResponse.from(getParcel.byNumber(tenantId, parcelNumber));
    }

    @GetMapping("/{parcelId}")
    public ParcelResponse get(@PathVariable String parcelId) {
        return ParcelResponse.from(getParcel.byId(parcelId));
    }

    @PutMapping("/{parcelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String parcelId, @Valid @RequestBody UpdateParcelRequest request) {
        updateParcel.execute(new UpdateParcelUseCase.Command(
                parcelId,
                request.region(),
                request.zone(),
                request.woreda(),
                request.kebele(),
                request.locality(),
                request.landUse(),
                request.tenureType(),
                request.areaSquareMeters(),
                request.titleDeedNumber(),
                request.leaseReference(),
                request.leaseExpiresAt()));
    }

    @PostMapping("/{parcelId}/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@PathVariable String parcelId, @Valid @RequestBody TransferRequest request) {
        transferParcel.execute(parcelId, request.citizenId(), request.organizationId());
    }

    @PostMapping("/{parcelId}/encumber")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void encumber(@PathVariable String parcelId, @Valid @RequestBody ReasonRequest request) {
        encumberParcel.execute(parcelId, request.reason());
    }

    @PostMapping("/{parcelId}/clear-encumbrance")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearEncumbrance(@PathVariable String parcelId) {
        clearEncumbrance.execute(parcelId);
    }

    @PostMapping("/{parcelId}/documents")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attachDocument(@PathVariable String parcelId, @Valid @RequestBody DocumentRequest request) {
        attachDocument.execute(parcelId, request.documentId());
    }

    @PostMapping("/{parcelId}/case")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkCase(@PathVariable String parcelId, @Valid @RequestBody CaseRequest request) {
        linkCase.execute(parcelId, request.caseId());
    }

    @PostMapping("/{parcelId}/retire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(@PathVariable String parcelId) {
        retireParcel.execute(parcelId);
    }

    @PostMapping("/{parcelId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String parcelId, @Valid @RequestBody ReasonRequest request) {
        cancelParcel.execute(parcelId, request.reason());
    }

    public record RegisterParcelRequest(
            @NotBlank String tenantId,
            @NotBlank String region,
            String zone,
            @NotBlank String woreda,
            String kebele,
            String locality,
            @NotBlank String landUse,
            @NotBlank String tenureType,
            @NotBlank String areaSquareMeters,
            String citizenId,
            String organizationId,
            String titleDeedNumber,
            String leaseReference,
            String leaseExpiresAt,
            String linkedCaseId) {}

    public record UpdateParcelRequest(
            @NotBlank String region,
            String zone,
            @NotBlank String woreda,
            String kebele,
            String locality,
            @NotBlank String landUse,
            @NotBlank String tenureType,
            @NotBlank String areaSquareMeters,
            String titleDeedNumber,
            String leaseReference,
            String leaseExpiresAt) {}

    public record TransferRequest(String citizenId, String organizationId) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record DocumentRequest(@NotBlank String documentId) {}

    public record CaseRequest(@NotBlank String caseId) {}

    public record ParcelIdResponse(String parcelId) {}

    public record ParcelResponse(
            String parcelId,
            String tenantId,
            String parcelNumber,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String landUse,
            String tenureType,
            String status,
            String areaSquareMeters,
            String citizenId,
            String organizationId,
            String titleDeedNumber,
            String leaseReference,
            String leaseExpiresAt,
            String linkedCaseId,
            Set<String> documentIds,
            String encumbranceReason) {

        static ParcelResponse from(LandParcel parcel) {
            return new ParcelResponse(
                    parcel.id().value(),
                    parcel.tenantId().value(),
                    parcel.parcelNumber().value(),
                    parcel.region(),
                    parcel.zone().orElse(null),
                    parcel.woreda(),
                    parcel.kebele().orElse(null),
                    parcel.locality().orElse(null),
                    parcel.landUse().name(),
                    parcel.tenureType().name(),
                    parcel.status().name(),
                    parcel.areaSquareMeters().toString(),
                    parcel.owner().citizenId().orElse(null),
                    parcel.owner().organizationId().orElse(null),
                    parcel.titleDeedNumber().orElse(null),
                    parcel.leaseReference().orElse(null),
                    parcel.leaseExpiresAt().map(java.time.Instant::toString).orElse(null),
                    parcel.linkedCaseId().orElse(null),
                    parcel.documentIds(),
                    parcel.encumbranceReason().orElse(null));
        }
    }
}
