package Utils;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.File;

@EnableBatchProcessing
@SpringBootApplication
class BatchConfiguration {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public BatchConfiguration(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory,
                              DataSource dataSource) {
		StepBuilderFactory stepBuilderFactory1 = stepBuilderFactory;
		JobBuilderFactory jobBuilderFactory1 = jobBuilderFactory;
		DataSource dataSource1 = dataSource;
    }

    public static void main(String[] args) {
        System.setProperty("input", "file:" + new File("/Users/teuno/IdeaProjects/funbatch/src/main/resources/CSV-files/combinedcsv.csv")
                .getAbsolutePath());
        System.setProperty("output", "file:" + new File("/Users/teuno/IdeaProjects/funbatch/src/main/resources/CSV-files/filefordemo.csv")
                .getAbsolutePath());

        SpringApplication.run(BatchConfiguration.class, args);
    }

    @Bean
    Job job(JobBuilderFactory jbf,
            StepBuilderFactory sbf,
            Step1 step1) throws Exception {

        Step s1 = sbf.get("file-db")
                .<OrderDTO, OrderDTO>chunk(100)
                .reader(step1.reader(null))
                .writer(step1.writer(null))
                .build();

        return jbf.get("etl")
                  .incrementer(new RunIdIncrementer())
                  .start(s1)
                  .next(s1)
                  .build();
    }


    @Configuration
    static class Step1 {
//		@Bean //hij zeikt over dubbele delimiter, maar die maak ik zelf niet aan.
//		FlatFileItemReader<OrderDTO> reader(@Value("${input}") Resource in) throws Exception {
//			return new FlatFileItemReaderBuilder<OrderDTO>()
//					.name("fed files")
//					.resource(in)
//					.linesToSkip(1)
//					.lineMapper(new DefaultLineMapper<OrderDTO>() {{
//						setLineTokenizer(new DelimitedLineTokenizer() {{
//							setNames(new String[] {"accountNumber", "name", "money", "date" });
//						}});
//						setFieldSetMapper(new OrderFieldSetMapper());
//					}}).build();
//		}

        @Bean
        public FlatFileItemReader<OrderDTO> reader(@Value("${input}") Resource in) throws Exception {
            FlatFileItemReader<OrderDTO> reader = new FlatFileItemReader<>();
            reader.setResource(in);
            reader.setLinesToSkip(1);
            reader.setLineMapper(new DefaultLineMapper<OrderDTO>() {{
                setLineTokenizer(new DelimitedLineTokenizer() {{
                    setNames(new String[]{"accountNumber", "name", "money", "date"});
                }});
                setFieldSetMapper(new OrderFieldSetMapper());
            }});
            return reader;
        }

        @Bean
        ItemWriter<OrderDTO> writer(@Value("${output}") Resource resource) {
            return new FlatFileItemWriterBuilder<OrderDTO>()
                    .name("file-writer")
                    .resource(resource)
                    .lineAggregator(new DelimitedLineAggregator<OrderDTO>() {
                        {
                            setDelimiter(",");
                            setFieldExtractor(orderDTO -> {
                                BeanWrapperFieldExtractor<OrderDTO> fieldExtractor = new BeanWrapperFieldExtractor<>();
                                fieldExtractor.setNames(new String[]{"accountNumber", "name", "money", "date"});
                                return fieldExtractor.extract(orderDTO);
                            });
                        }
                    })
                    .append(true)
                    .build();
        }
    }
}