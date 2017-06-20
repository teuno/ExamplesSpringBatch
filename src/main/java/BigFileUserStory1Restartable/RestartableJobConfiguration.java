package BigFileUserStory1Restartable;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RestartableJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Autowired
    public RestartableJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, @Qualifier("dataSource") DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public Job processOrderJob() {
        return jobBuilderFactory.get("processOrderJob")
                                .incrementer(new RunIdIncrementer())
                                .flow(orderStep())
                                .end() //afterjob @AfterJob empty jobID in database key-value
                                .listener(new WriteToDatabaseAfterJob())
                                .build();
    }

    @Bean
    public Step orderStep() {
        return stepBuilderFactory.get("orderStep").<OrderDTO, OrderDTO>chunk(500)
                .reader(readFromCSV()).writer(writeToOrderTable())
                .allowStartIfComplete(true)
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
}