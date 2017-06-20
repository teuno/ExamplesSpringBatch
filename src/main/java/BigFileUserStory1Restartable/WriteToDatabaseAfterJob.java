package BigFileUserStory1Restartable;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class WriteToDatabaseAfterJob implements JobExecutionListener {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void beforeJob(JobExecution jobExecution) {
        //
        // Can Log || do some business code
        //
        String jobName = jobExecution.getJobInstance().getJobName();
        Long jobExecutionID = jobExecution.getId();
        log.info("jobname " + jobName + " jobExecutionID " + jobExecutionID);
        log.info("Intercepting Job Excution - Before Job!");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        //
        // write key value from jobname-jobInstanceID set instanceID
        //
        if (jobExecution.getExitStatus() != ExitStatus.COMPLETED) {
            String jobName = jobExecution.getJobInstance().getJobName();
            Long jobExecutionID = jobExecution.getId();
            log.info("jobname " + jobName + " jobExecutionID " + jobExecutionID + " inserted in DB");
        }

        log.info("Intercepting Job Excution - After Job!");
    }

}