package et.gov.ethgov.workflow.application.instance;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.workflow.domain.instance.ProcessInstance;
import et.gov.ethgov.workflow.domain.instance.ProcessInstanceId;
import et.gov.ethgov.workflow.domain.instance.ProcessInstanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetProcessInstanceUseCase {

    private final ProcessInstanceRepository instances;

    public GetProcessInstanceUseCase(ProcessInstanceRepository instances) {
        this.instances = instances;
    }

    @Transactional(readOnly = true)
    public ProcessInstance byId(String processInstanceId) {
        return instances
                .findById(ProcessInstanceId.of(processInstanceId))
                .orElseThrow(() -> new DomainException(
                        "PROCESS_NOT_FOUND",
                        "Process Not Found",
                        "Process instance does not exist",
                        404));
    }
}
