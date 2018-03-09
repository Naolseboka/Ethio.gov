package et.gov.ethgov.notification;

import et.gov.ethgov.platform.persistence.inbox.InboxMessageEntity;
import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"et.gov.ethgov.notification", "et.gov.ethgov.platform.web"})
@EntityScan(
        basePackageClasses = {
            et.gov.ethgov.notification.adapter.out.persistence.NotificationJpaEntity.class,
            et.gov.ethgov.notification.adapter.out.persistence.NotificationNumberSequenceEntity.class,
            OutboxMessageEntity.class,
            InboxMessageEntity.class
        })
@EnableJpaRepositories(basePackages = "et.gov.ethgov.notification.adapter.out.persistence")
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
