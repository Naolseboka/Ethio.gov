package et.gov.ethgov.workflow.application;

import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionDomainEvent;
import et.gov.ethgov.workflow.domain.instance.ProcessInstanceDomainEvent;
import et.gov.ethgov.workflow.domain.task.WorkflowTaskDomainEvent;

public interface WorkflowEventPublisher {

    void publishDefinitionEvents(Iterable<WorkflowDefinitionDomainEvent> events);

    void publishInstanceEvents(Iterable<ProcessInstanceDomainEvent> events);

    void publishTaskEvents(Iterable<WorkflowTaskDomainEvent> events);
}
