package et.gov.ethgov.gateway;

import et.gov.ethgov.platform.persistence.inbox.InboxMessageEntity;
import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"et.gov.ethgov.gateway", "et.gov.ethgov.platform.web"})
@EntityScan(
        basePackageClasses = {
            et.gov.ethgov.gateway.adapter.out.persistence.RateLimitPolicyJpaEntity.class,
            et.gov.ethgov.gateway.adapter.out.persistence.RateCounterJpaEntity.class,
            et.gov.ethgov.gateway.adapter.out.persistence.ResiliencePolicyJpaEntity.class,
            et.gov.ethgov.gateway.adapter.out.persistence.AuditEntryJpaEntity.class,
            et.gov.ethgov.gateway.adapter.out.persistence.RateLimitSequenceEntity.class,
            et.gov.ethgov.gateway.adapter.out.persistence.ResilienceSequenceEntity.class,
            OutboxMessageEntity.class,
            InboxMessageEntity.class
        })
@EnableJpaRepositories(basePackages = "et.gov.ethgov.gateway.adapter.out.persistence")
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
