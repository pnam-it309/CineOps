package service.cinema.be;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
@EnableTransactionManagement
public class BeApplication {

    public static void main(String[] args) {
        // Load .env variables into System properties
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // Root of the project where .env is located
                .ignoreIfMissing()
                .load();
        
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        SpringApplication.run(BeApplication.class, args);
    }

}
