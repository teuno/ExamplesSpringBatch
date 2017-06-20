package OrdersEnRegelsAanmakenVanStortingsbestand;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@EnableBatchProcessing
@SpringBootApplication
class BatchConfiguration
{
	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	private final DataSource dataSource;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
	BatchConfiguration(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory,
                       DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.dataSource = dataSource;
    }

	public static void main(String[] args)
	{
		SpringApplication.run(BatchConfiguration.class, args);
	}

	@Bean
	Job importStortingJob() {
		return jobBuilderFactory.get("import Storting Job")
                                .incrementer(new RunIdIncrementer())
                                .start(flow())
                                .end()
                                .build();
    }

    @Bean
	Flow flow() {
		FlowBuilder<Flow> flowbuilder = new FlowBuilder<>("mainflow");

        return flowbuilder.start(readFromCSVToDatabase())
                          .from(readFromCSVToDatabase())
                          .on("COMPLETED").to(createOrderLines())
                          .from(createOrderLines())
                          .on("*")
                          .end()
                          .build();
    }

	@Bean
    Step readFromCSVToDatabase() {
        return stepBuilderFactory.get("step1")
				.<OrderDTO, OrderDTO>chunk(500)
                .reader(readFromCSV())
                .writer(writeToOrderTable())
                .faultTolerant().skipPolicy(stepListener())
                .build();
    }

	@Bean
    FlatFileItemReader<OrderDTO> readFromCSV() {
        FlatFileItemReader<OrderDTO> reader = new FlatFileItemReader<>();

		String fileToRead = "CSV-files/combinedcsv.csv";

		reader.setResource(new ClassPathResource(fileToRead));
        reader.setLineMapper(getLineMapper());
        return reader;
    }

    private DefaultLineMapper<OrderDTO> getLineMapper() {
        DefaultLineMapper<OrderDTO> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new OrderFieldSetMapper());
        return lineMapper;
    }

	@Bean
    JdbcBatchItemWriter<OrderDTO> writeToOrderTable() {
        JdbcBatchItemWriter<OrderDTO> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource);
		writer.setJdbcTemplate(new NamedParameterJdbcTemplate(dataSource));

		String QUERY_INSERT = "INSERT INTO order_ "
			+ "(Sum_, InputDate, AccountNumber, Status) VALUES (?, ?, ?, 'REGISTERED')";

		writer.setSql(QUERY_INSERT);

        writer.setItemPreparedStatementSetter(new OrderPreparedStatementSetter());

		return writer;
	}

	@Bean
	SkipPolicy stepListener()
	{
        return new OrderToDatabaseListener();
    }

	@Bean
    Step createOrderLines() {
        return stepBuilderFactory.get("step2")
				.<OrderLineDTO, OrderLineDTO>chunk(500)
                .reader(orderIDItemReader())
                .writer(orderLineWriter())
                .build();
    }
	@Bean
    ItemReader<OrderLineDTO> orderIDItemReader() {
        JdbcCursorItemReader<OrderLineDTO> databaseReader = new JdbcCursorItemReader<>();

		databaseReader.setDataSource(dataSource);
		int amountToRead = 500;
		databaseReader.setFetchSize(amountToRead);

		String GET_ORDER_FONDS__IDS = "SELECT fund_.ID as fonds_id, order_.ID as OrderID\n" + "FROM fund_, order_\n"
			+ "WHERE order_.Status = 'REGISTERED'" + "AND fund_.Percentage > 0";
		databaseReader.setSql(GET_ORDER_FONDS__IDS);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(OrderLineDTO.class));

		return databaseReader;
	}

	@Bean
    JdbcBatchItemWriter<OrderLineDTO> orderLineWriter() {
        JdbcBatchItemWriter<OrderLineDTO> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setJdbcTemplate(new NamedParameterJdbcTemplate(dataSource));

		String INSERT_ORDERREGEL = "INSERT INTO orderline_ (OrderID, FundID, Status) VALUES (?,?,'REGISTERED')";
		writer.setSql(INSERT_ORDERREGEL);

		writer.setItemPreparedStatementSetter(new OrderRegelPreparedStatementSetter());

		return writer;
	}
}