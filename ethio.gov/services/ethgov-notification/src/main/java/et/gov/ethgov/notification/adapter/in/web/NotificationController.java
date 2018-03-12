package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.message.CancelNotificationUseCase;
import et.gov.ethgov.notification.application.message.GetNotificationUseCase;
import et.gov.ethgov.notification.application.message.MarkDeliveredUseCase;
import et.gov.ethgov.notification.application.message.MarkFailedUseCase;
import et.gov.ethgov.notification.application.message.MarkSentUseCase;
import et.gov.ethgov.notification.application.message.ScheduleNotificationUseCase;
import et.gov.ethgov.notification.domain.message.OutboundNotification;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

    private final ScheduleNotificationUseCase scheduleNotification;
    private final MarkSentUseCase markSent;
    private final MarkDeliveredUseCase markDelivered;
    private final MarkFailedUseCase markFailed;
    private final CancelNotificationUseCase cancelNotification;
    private final GetNotificationUseCase getNotification;

    public NotificationController(
            ScheduleNotificationUseCase scheduleNotification,
            MarkSentUseCase markSent,
            MarkDeliveredUseCase markDelivered,
            MarkFailedUseCase markFailed,
            CancelNotificationUseCase cancelNotification,
            GetNotificationUseCase getNotification) {
        this.scheduleNotification = scheduleNotification;
        this.markSent = markSent;
        this.markDelivered = markDelivered;
        this.markFailed = markFailed;
        this.cancelNotification = cancelNotification;
        this.getNotification = getNotification;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationIdResponse schedule(@Valid @RequestBody ScheduleRequest request) {
        return new NotificationIdResponse(scheduleNotification.execute(new ScheduleNotificationUseCase.Command(
                        request.tenantId(),
                        request.channel(),
                        request.phone(),
                        request.email(),
                        request.deviceToken(),
                        request.locale(),
                        request.subject(),
                        request.body(),
                        request.templateCode(),
                        request.citizenId(),
                        request.organizationId(),
                        request.sourceReference()))
                .value());
    }

    @GetMapping("/lookup")
    public NotificationResponse lookup(
            @RequestParam String tenantId, @RequestParam String notificationNumber) {
        return NotificationResponse.from(getNotification.byNumber(tenantId, notificationNumber));
    }

    @GetMapping("/{notificationId}")
    public NotificationResponse get(@PathVariable String notificationId) {
        return NotificationResponse.from(getNotification.byId(notificationId));
    }

    @PostMapping("/{notificationId}/sent")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sent(@PathVariable String notificationId, @Valid @RequestBody SentRequest request) {
        markSent.execute(notificationId, request.providerReference());
    }

    @PostMapping("/{notificationId}/delivered")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delivered(@PathVariable String notificationId) {
        markDelivered.execute(notificationId);
    }

    @PostMapping("/{notificationId}/fail")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void fail(@PathVariable String notificationId, @Valid @RequestBody ReasonRequest request) {
        markFailed.execute(notificationId, request.reason());
    }

    @PostMapping("/{notificationId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String notificationId, @Valid @RequestBody ReasonRequest request) {
        cancelNotification.execute(notificationId, request.reason());
    }

    public record ScheduleRequest(
            @NotBlank String tenantId,
            @NotBlank String channel,
            String phone,
            String email,
            String deviceToken,
            String locale,
            String subject,
            @NotBlank @Size(max = 4000) String body,
            String templateCode,
            String citizenId,
            String organizationId,
            String sourceReference) {}

    public record SentRequest(@NotBlank @Size(min = 5, max = 120) String providerReference) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record NotificationIdResponse(String notificationId) {}

    public record NotificationResponse(
            String notificationId,
            String tenantId,
            String notificationNumber,
            String channel,
            String status,
            String phone,
            String email,
            String deviceToken,
            String locale,
            String subject,
            String body,
            String templateCode,
            String providerReference,
            String sourceReference) {

        static NotificationResponse from(OutboundNotification notification) {
            return new NotificationResponse(
                    notification.id().value(),
                    notification.tenantId().value(),
                    notification.notificationNumber().value(),
                    notification.channel().name(),
                    notification.status().name(),
                    notification.recipient().phoneE164().orElse(null),
                    notification.recipient().email().orElse(null),
                    notification.recipient().deviceToken().orElse(null),
                    notification.locale().value(),
                    notification.subject().orElse(null),
                    notification.body(),
                    notification.templateCode().orElse(null),
                    notification.providerReference().orElse(null),
                    notification.sourceReference().orElse(null));
        }
    }
}
