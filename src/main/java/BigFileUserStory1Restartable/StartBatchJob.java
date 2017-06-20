package BigFileUserStory1Restartable;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class StartBatchJob {
    public static void main(String[] args) {
        SpringApplication.run(StartBatchJob.class, args);
    }
}
