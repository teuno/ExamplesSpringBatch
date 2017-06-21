package TransactionJob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
@EnableBatchProcessing
public class ConfigureAndLaunch
{

	public static void main(String[] args)
	{
		String[] springConfig = { "TransactionJobConfig.xml" };
		ApplicationContext context = new ClassPathXmlApplicationContext(springConfig);
		JobLauncher jobLauncher = (JobLauncher) context.getBean(("jobLauncher"));
		Job job = (Job) context.getBean(("OrderlinetoTransactionJob"));
		try
		{

			JobExecution execution = jobLauncher.run(job, new JobParameters());
			System.out.println("Exit Status : " + execution.getStatus());

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
