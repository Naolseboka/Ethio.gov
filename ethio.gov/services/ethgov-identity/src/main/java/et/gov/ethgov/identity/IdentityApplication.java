package et.gov.ethgov.identity;

import et.gov.ethgov.platform.persistence.inbox.InboxMessageEntity;
import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Clock;

@SpringBootApplication(scanBasePackages = {
        "et.gov.ethgov.identity",
        "et.gov.ethgov.platform.web"
})
@EntityScan(basePackageClasses = {
        et.gov.ethgov.identity.adapter.out.persistence.UserAccountJpaEntity.class,
        OutboxMessageEntity.class,
        InboxMessageEntity.class
})
@EnableJpaRepositories(basePackages = "et.gov.ethgov.identity.adapter.out.persistence")
public class IdentityApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
