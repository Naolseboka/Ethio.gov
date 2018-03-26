package et.gov.ethgov.opendata;

import et.gov.ethgov.platform.persistence.inbox.InboxMessageEntity;
import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"et.gov.ethgov.opendata", "et.gov.ethgov.platform.web"})
@EntityScan(
        basePackageClasses = {
            et.gov.ethgov.opendata.adapter.out.persistence.DatasetJpaEntity.class,
            et.gov.ethgov.opendata.adapter.out.persistence.ResourceJpaEntity.class,
            et.gov.ethgov.opendata.adapter.out.persistence.DatasetNumberSequenceEntity.class,
            et.gov.ethgov.opendata.adapter.out.persistence.ResourceNumberSequenceEntity.class,
            OutboxMessageEntity.class,
            InboxMessageEntity.class
        })
@EnableJpaRepositories(basePackages = "et.gov.ethgov.opendata.adapter.out.persistence")
public class OpenDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenDataApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
