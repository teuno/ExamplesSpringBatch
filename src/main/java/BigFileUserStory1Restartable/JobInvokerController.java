package BigFileUserStory1Restartable;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class JobInvokerController {

    private final JobLauncher jobLauncher;

    private final JobOperator jobOperator;

    private final Job processOrderJob;

    private final JobExplorer jobExplorer;

    private Long JOB_ID; //get JobID in database key-value

    @Autowired
    public JobInvokerController(JobLauncher jobLauncher, JobOperator jobOperator, Job processOrderJob, JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.jobOperator = jobOperator;
        this.processOrderJob = processOrderJob;
        this.jobExplorer = jobExplorer;

        this.JOB_ID = getJobIDFromDatabase();
    }

    private Long getJobIDFromDatabase() {
        return 0L; //read from database
    }

    @RequestMapping("/invokejob")
    public String handle() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()//.addLong("time", System.currentTimeMillis())
                                                                .toJobParameters();

        if (JOB_ID == 0L) {
            jobLauncher.run(processOrderJob, jobParameters);
        } else {//restart only works when restart server...
            jobOperator.restart(1L);
        }
        return "Batch job has been invoked";
    }

    @RequestMapping("/stopjob")
    public String stopHandle() throws Exception {

        Set<Long> executions = jobOperator.getRunningExecutions("processOrderJob");
        JOB_ID = executions.iterator().next(); //jobExecutionID
        jobOperator.stop(JOB_ID);

        return "Batch job has been invoked";
    }
}
