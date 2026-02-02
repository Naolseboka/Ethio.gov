package et.gov.ethgov.workflow.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.workflow.domain.definition.DefinitionKey;
import et.gov.ethgov.workflow.domain.definition.StepType;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinition;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionStatus;
import et.gov.ethgov.workflow.domain.definition.WorkflowStep;
import et.gov.ethgov.workflow.domain.instance.ProcessInstance;
import et.gov.ethgov.workflow.domain.instance.ProcessInstanceStatus;
import et.gov.ethgov.workflow.domain.task.WorkflowTask;
import et.gov.ethgov.workflow.domain.task.WorkflowTaskStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkflowDomainTest {

    private final Instant now = Instant.parse("2024-06-01T00:00:00Z");
    private final TenantId tenantId = TenantId.generate();
    private final String user = "usr_11111111-1111-4111-8111-111111111111";

    @Test
    void publishesDefinitionAndStartsProcess() {
        WorkflowDefinition definition = definition();
        definition.publish(now);
        assertThat(definition.status()).isEqualTo(WorkflowDefinitionStatus.PUBLISHED);

        ProcessInstance instance = ProcessInstance.start(
                tenantId, definition.id(), "BL-2024-001", user, definition.requireStep(0), now);
        assertThat(instance.status()).isEqualTo(ProcessInstanceStatus.RUNNING);

        WorkflowTask task = WorkflowTask.open(
                tenantId, instance.id(), "intake", "Intake Review", "clerk", now);
        task.claim(user, now);
        task.complete("APPROVED", user, now);
        assertThat(task.status()).isEqualTo(WorkflowTaskStatus.COMPLETED);
    }

    @Test
    void rejectsClaimMismatchOnComplete() {
        WorkflowDefinition definition = definition();
        definition.publish(now);
        ProcessInstance instance = ProcessInstance.start(
                tenantId, definition.id(), "BL-2024-002", user, definition.requireStep(0), now);
        WorkflowTask task = WorkflowTask.open(
                tenantId, instance.id(), "intake", "Intake Review", "clerk", now);
        task.claim(user, now);
        assertThatThrownBy(() -> task.complete("APPROVED", "usr_22222222-2222-4222-8222-222222222222", now))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("claiming user");
    }

    private WorkflowDefinition definition() {
        return WorkflowDefinition.create(
                tenantId,
                DefinitionKey.of("business-license"),
                "Business License",
                "Municipal business licensing",
                List.of(
                        WorkflowStep.of("intake", "Intake Review", StepType.USER_TASK, "clerk", 0),
                        WorkflowStep.of("approve", "Manager Approval", StepType.USER_TASK, "manager", 1)),
                now);
    }
}
