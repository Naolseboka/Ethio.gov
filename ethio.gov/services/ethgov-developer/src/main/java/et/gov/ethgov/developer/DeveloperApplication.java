package et.gov.ethgov.developer;

import et.gov.ethgov.platform.persistence.inbox.InboxMessageEntity;
import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"et.gov.ethgov.developer", "et.gov.ethgov.platform.web"})
@EntityScan(
        basePackageClasses = {
            et.gov.ethgov.developer.adapter.out.persistence.DeveloperAppJpaEntity.class,
            et.gov.ethgov.developer.adapter.out.persistence.ApiCredentialJpaEntity.class,
            et.gov.ethgov.developer.adapter.out.persistence.AppNumberSequenceEntity.class,
            et.gov.ethgov.developer.adapter.out.persistence.CredentialNumberSequenceEntity.class,
            OutboxMessageEntity.class,
            InboxMessageEntity.class
        })
@EnableJpaRepositories(basePackages = "et.gov.ethgov.developer.adapter.out.persistence")
public class DeveloperApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeveloperApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
