package CourseJob;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
public class BatchConfiguration
{
	@Qualifier("dataSource")
	@Autowired
	private DataSource dataSource;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	public static void main(String[] args) throws Exception
	{
		SpringApplication app = new SpringApplication(BatchConfiguration.class);
		ConfigurableApplicationContext ctx = app.run(args);

		JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
		Job job = ctx.getBean("importUserJob", Job.class);
		String[][] Data = new String[][] { { "20170606", "3" }, { "20170606", "1" } };
		for (String[] item : Data)
		{
			JobParameters jobParameters = new JobParametersBuilder().addString("TradeDate", item[0])
				.addString("FundID", item[1]).toJobParameters();
			jobLauncher.run(job, jobParameters);
		}
	}

	@Bean
	Job importUserJob()
	{
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).start(shareProcessorStep())
			.next(orderlineUpdaterStep()).next(orderUpdaterStep()).build();

	}

	@Bean
	Step shareProcessorStep()
	{
		String OverRiddenDate = null;
		String OverridenFundID = null;
		return stepBuilderFactory.get("shareProcessorStep").<TransactionShareRateDTO, SharesDTO> chunk(100)
			.reader(transactionShareRateReader(OverRiddenDate, OverridenFundID))
			.processor(new TransactionToSharesProcessor()).writer(shareWriter())
			.build();
	}


	@Bean
	Step orderlineUpdaterStep()
	{

		SingleSQLStatementTasklet orderlineTasklet = new SingleSQLStatementTasklet();
		orderlineTasklet.setDataSource(dataSource);
		orderlineTasklet.setSql("UPDATE orderline_\n" + "set fundation.orderline_.STATUS='Completed'\n"
			+ "where orderline_.ID in (\n" + "select trx_.OrderlineID\n"
			+ "from trx_ join rate_ on trx_.FundID = rate_.FundID and trx_.TradeDate = rate_.TradeDate);\n");
		return stepBuilderFactory.get("orderlineUpdaterStep").tasklet(orderlineTasklet).build();
	}

	@Bean
	Step orderUpdaterStep()
	{
		SingleSQLStatementTasklet orderTasklet = new SingleSQLStatementTasklet();
		orderTasklet.setDataSource(dataSource);
		orderTasklet.setSql("update order_\n" + "set fundation.order_.Status='completed'\n"
			+ "where ID not in ( select DISTINCT (orderline_.OrderID)\n" + "from orderline_\n"
			+ "WHERE orderline_.status <> 'completed');");
		return stepBuilderFactory.get("orderUpdaterStep").tasklet(orderTasklet).build();
	}

	@Bean
	@StepScope
	JdbcCursorItemReader<TransactionShareRateDTO> transactionShareRateReader(
		@Value("#{jobParameters['TradeDate']}") String tradeDate, @Value("#{jobParameters['FundID']}") String fundID)
	{
		String QUERY_GET_TSRDATA = "select order_.AccountNumber, trx_.TradeDate, trx_.Sum_ as TransactionSum, trx_.FundID, rate_.ShareRate, trx_.ThrowsException TransactionException, rate_.ThrowsException as SharesException\n"
			+ "from trx_\n" + "join orderline_ on trx_.OrderlineID = orderline_.ID\n"
			+ "Join order_ on orderline_.OrderID = order_.ID\n" + "join rate_ on trx_.FundID = rate_.FundID\n"
			+ "where trx_.TradeDate = " + tradeDate + " and rate_.fundID = " + fundID;
		JdbcCursorItemReader<TransactionShareRateDTO> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setRowMapper(new TransactionShareRateMapper());
		reader.setSql(QUERY_GET_TSRDATA);
		return reader;
	}

	@Bean
	JdbcBatchItemWriter shareWriter()
	{
		String QUERY_SET_SHAREDATA = "insert into shares_ (FundID, AccountNumber, Amount) values (?, ?, ?)\n"
			+ " ON DUPLICATE KEY UPDATE Amount=Amount+?;";
		JdbcBatchItemWriter<SharesDTO> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource);
		writer.setSql(QUERY_SET_SHAREDATA);
		writer.setItemPreparedStatementSetter(new SharesPreparedStatementSetter());
		return writer;
	}

}
