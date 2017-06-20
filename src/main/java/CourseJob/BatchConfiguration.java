package CourseJob;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
public class BatchConfiguration
{
	@Autowired
	JobRepository jobRepository;

	@Qualifier("dataSource")
	@Autowired
	DataSource dataSource;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	public static void main(String[] args)
	{
		SpringApplication.run(BatchConfiguration.class, args);
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

		ArrayList<ArrayList<String>> bits = new ArrayList<>();
		ArrayList<String> bats = new ArrayList<>();
		bats.add("1");
		bats.add("2017-05-25");
		bits.add(bats);
		bats.clear();
		bats.add("2");
		bats.add("2017-05-25");
		bits.add(bats);

		return stepBuilderFactory.get("shareProcessorStep").<TransactionShareRateDTO, SharesDTO> chunk(100)
			.reader(transactionShareRateReader()).processor(new TransactionToSharesProcessor()).writer(shareWriter())
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
	JdbcCursorItemReader<TransactionShareRateDTO> transactionShareRateReader()
	{

		String QUERY_GET_TSRDATA = "select order_.AccountNumber, trx_.TradeDate, trx_.Sum_ as TransactionSum, trx_.FundID, rate_.ShareRate, trx_.ThrowsException TransactionException, rate_.ThrowsException as SharesException\n"
			+ "from trx_\n" + "join orderline_ on trx_.OrderlineID = orderline_.ID\n"
			+ "Join order_ on orderline_.OrderID = order_.ID\n" + "join rate_ on trx_.FundID = rate_.FundID\n"; // +
		// "WHERE trx_.TradeDate='2017-05-24'\n" +
		// "AND rate_.TradeDate='2017-05-24'\n";

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
