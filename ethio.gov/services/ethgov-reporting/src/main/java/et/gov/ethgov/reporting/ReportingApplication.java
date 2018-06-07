package et.gov.ethgov.reporting;

import et.gov.ethgov.platform.persistence.inbox.InboxMessageEntity;
import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"et.gov.ethgov.reporting", "et.gov.ethgov.platform.web"})
@EntityScan(
        basePackageClasses = {
            et.gov.ethgov.reporting.adapter.out.persistence.ReportDefinitionJpaEntity.class,
            et.gov.ethgov.reporting.adapter.out.persistence.ReportRunJpaEntity.class,
            et.gov.ethgov.reporting.adapter.out.persistence.ReportNumberSequenceEntity.class,
            et.gov.ethgov.reporting.adapter.out.persistence.RunNumberSequenceEntity.class,
            OutboxMessageEntity.class,
            InboxMessageEntity.class
        })
@EnableJpaRepositories(basePackages = "et.gov.ethgov.reporting.adapter.out.persistence")
public class ReportingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
