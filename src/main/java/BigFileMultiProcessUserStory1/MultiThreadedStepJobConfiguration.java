package BigFileMultiProcessUserStory1;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.amqp.AmqpItemReader;
import org.springframework.batch.item.amqp.AmqpItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class MultiThreadedStepJobConfiguration {

    private final JobBuilderFactory jobBuilders;

    private final StepBuilderFactory stepBuilders;

    private final DataSource dataSource;

    private final IMultiProcessConfiguration processConfiguration;

    private final int CHUNK_SIZE = 100;
    private final String FILE_TO_READ_LOCATION = "CSV-files/filefordemo.csv";

    @Autowired
    public MultiThreadedStepJobConfiguration(JobBuilderFactory jobBuilders, StepBuilderFactory stepBuilders, @Qualifier("dataSource") DataSource dataSource, IMultiProcessConfiguration processConfiguration) {
        this.jobBuilders = jobBuilders;
        this.stepBuilders = stepBuilders;
        this.dataSource = dataSource;
        this.processConfiguration = processConfiguration;
    }

    public static void main(String[] args) {
        SpringApplication.run(MultiThreadedStepJobConfiguration.class, args);
    }

    @Bean
    public Job multiThreadedStepJob() {
        Flow flow1 = new FlowBuilder<Flow>("subflow1").from(readFromCSVToMessageQueue()).end();
        Flow flow2 = new FlowBuilder<Flow>("subflow2").from(writeOrdersWithDataFromMessageQueue()).end();

        Flow splitFlow = new FlowBuilder<Flow>("splitflow").split(processConfiguration.taskExecutor())
                                                           .add(flow1, flow2).build();

        return jobBuilders.get("multiThreadedStepJob")
                          .start(splitFlow)
                          .next(createOrderLines())
                          .end()
                          .build();
    }

    @Bean
    public Step readFromCSVToMessageQueue() {
        return stepBuilders.get("step")
                .<OrderDTO, OrderDTO>chunk(CHUNK_SIZE)
                .reader(readFromCSV())
                .writer(writeToMessageQueue())
                //                .faultTolerant().skipPolicy(stepListener())
                .build();
    }


    @Bean
    public FlatFileItemReader<OrderDTO> readFromCSV() {
        Resource fileToRead = new ClassPathResource(FILE_TO_READ_LOCATION);

//        return new FlatFileItemReaderBuilder<OrderDTO>()
//                .name("read csv")
//                .resource(fileToRead)
//                .lineMapper(getLineMapper())
//                .build();

        FlatFileItemReader<OrderDTO> reader = new FlatFileItemReader<>();

        reader.setResource(fileToRead);
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
    SkipPolicy stepListener() {
        return new OrderToDatabaseListener();
    }

    @Bean
    public AmqpItemWriter<OrderDTO> writeToMessageQueue() {
        AmqpItemWriter<OrderDTO> writer = new AmqpItemWriter<>(processConfiguration
                .ampqTemplate(processConfiguration.connectionFactory()));
        return writer;
    }

    @Bean
    public Step writeOrdersWithDataFromMessageQueue() {
        return stepBuilders.get("step2")
                .<OrderDTO, OrderDTO>chunk(CHUNK_SIZE)
                .reader(readFromMessageQueue())
                .writer(writeToOrderTable())
                .throttleLimit(20) //default 4
                .taskExecutor(processConfiguration.taskExecutor())
                .build();
    }

    @Bean
    public AmqpItemReader<OrderDTO> readFromMessageQueue() {
        AmqpItemReader<OrderDTO> reader = new AmqpItemReader<>(processConfiguration
                .ampqTemplate(processConfiguration.connectionFactory()));
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<OrderDTO> writeToOrderTable() {
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
    Step createOrderLines() {
        return stepBuilders.get("step3")
                .<OrderLineDTO, OrderLineDTO>chunk(CHUNK_SIZE)
                .reader(orderIDItemReader())
                .writer(orderLineWriter())
                .build();
    }

    @Bean
    ItemReader<OrderLineDTO> orderIDItemReader() {
        JdbcCursorItemReader<OrderLineDTO> databaseReader = new JdbcCursorItemReader<>();

        databaseReader.setDataSource(dataSource);

        databaseReader.setFetchSize(CHUNK_SIZE);

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
