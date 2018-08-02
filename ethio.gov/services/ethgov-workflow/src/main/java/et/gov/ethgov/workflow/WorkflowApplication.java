package et.gov.ethgov.workflow;

import et.gov.ethgov.platform.persistence.inbox.InboxMessageEntity;
import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"et.gov.ethgov.workflow", "et.gov.ethgov.platform.web"})
@EntityScan(
        basePackageClasses = {
            et.gov.ethgov.workflow.adapter.out.persistence.WorkflowDefinitionJpaEntity.class,
            et.gov.ethgov.workflow.adapter.out.persistence.WorkflowStepJpaEntity.class,
            et.gov.ethgov.workflow.adapter.out.persistence.ProcessInstanceJpaEntity.class,
            et.gov.ethgov.workflow.adapter.out.persistence.WorkflowTaskJpaEntity.class,
            OutboxMessageEntity.class,
            InboxMessageEntity.class
        })
@EnableJpaRepositories(basePackages = "et.gov.ethgov.workflow.adapter.out.persistence")
public class WorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
