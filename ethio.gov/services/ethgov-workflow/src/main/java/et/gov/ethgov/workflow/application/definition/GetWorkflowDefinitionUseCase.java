package et.gov.ethgov.workflow.application.definition;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinition;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionId;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetWorkflowDefinitionUseCase {

    private final WorkflowDefinitionRepository definitions;

    public GetWorkflowDefinitionUseCase(WorkflowDefinitionRepository definitions) {
        this.definitions = definitions;
    }

    @Transactional(readOnly = true)
    public WorkflowDefinition byId(String definitionId) {
        return definitions
                .findById(WorkflowDefinitionId.of(definitionId))
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Workflow definition does not exist",
                        404));
    }
}
