package TransactiesAggregerenEnStatussenUpdaten;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@EnableBatchProcessing
@SpringBootApplication
class TransactiesAggregerenEnStatussenUpdaten {

    public static void main(String[] args) {
        SpringApplication.run(TransactiesAggregerenEnStatussenUpdaten.class, args);
    }

    @Bean
    Job AggregateTransactionsFromDatabaseToDatabase(JobBuilderFactory jobBuilderFactory,
                                                    StepBuilderFactory stepBuilderFactory,
                                                    Tasklets tasklets,
                                                    WriteDataToTempTable writeDataToTempTable,
                                                    FillStockOrderWithData fillStockOrderWithData
    ) {

        return jobBuilderFactory.get("Aggregeren")
                                .incrementer(new RunIdIncrementer())//static inner classes that need a datasource get a null in the constructor so they can reach a datasource
                                .start(tasklets.createTempTableStep(null, stepBuilderFactory))
                                .next(writeDataToTempTable.writeTransactionsToTempTableStep(stepBuilderFactory))
                                .next(fillStockOrderWithData.fillStockOrderStep(stepBuilderFactory))
                                .next(tasklets.updateOrderAndOrderlineStatusStep(null, stepBuilderFactory))
                                .next(tasklets.destroyTempTableStep(null, stepBuilderFactory))
                                .build();
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Configuration
    static class Tasklets {
        @Bean
        Step createTempTableStep(DataSource dataSource, StepBuilderFactory stepBuilderFactory) {
            StepBuilder createTempTable = stepBuilderFactory.get("create temp table");
            String sql = "CREATE TABLE temp_for_story3\n" +
                    "(\n" +
                    "    fund_name VARCHAR(255) NOT NULL,\n" +
                    "    trade_date DATE NOT NULL,\n" +
                    "    money DECIMAL NOT NULL,\n" +
                    "    order_id BIGINT NOT NULL,\n" +
                    "    orderline_id BIGINT NOT NULL\n" +
                    ")";
            return createTempTable.tasklet((contribution, chunkContext) -> {
                new JdbcTemplate(dataSource).execute(sql);
                return RepeatStatus.FINISHED;
            })
                                  .allowStartIfComplete(true)
                                  .build();
        }

        @Bean
        Step destroyTempTableStep(DataSource dataSource, StepBuilderFactory stepBuilderFactory) {
            StepBuilder destroyTempTable = stepBuilderFactory.get("destroy temp table");
            String sql = "DROP TABLE temp_for_story3";

            return destroyTempTable.tasklet((contribution, chunkContext) -> {
                new JdbcTemplate(dataSource).execute(sql);
                return RepeatStatus.FINISHED;
            })
                                   .allowStartIfComplete(true)
                                   .build();
        }

        @Bean
        Step updateOrderAndOrderlineStatusStep(DataSource dataSource, StepBuilderFactory stepBuilderFactory) {
            StepBuilder updateOrderAndOrderline = stepBuilderFactory.get("update order and orderline status");
            String sql = "UPDATE order_ as o LEFT JOIN orderline_ as ol ON o.ID = ol.OrderID\n" +
                    "SET o.Status = 'ORDERED', ol.STATUS = 'ORDERED'\n" +
                    "WHERE o.Status = 'CHECKED'";

            return updateOrderAndOrderline.tasklet((contribution, chunkContext) -> {
                new JdbcTemplate(dataSource).execute(sql);
                return RepeatStatus.FINISHED;
            })
                                          .allowStartIfComplete(true)
                                          .build();
        }
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Configuration
    static class WriteDataToTempTable {
        @Bean
        JdbcCursorItemReader<TempDataTableDTO> readTransactionsFromDatabase(DataSource dataSource) {
            return new JdbcCursorItemReaderBuilder<TempDataTableDTO>()
                    .dataSource(dataSource)
                    .name("read transactions")
                    .sql("SELECT f.Name as fundName, t.TradeDate as tradeDate, t.Sum_ as sum, o.ID as orderID, ol.ID as orderlineID\n" +
                            "FROM trx_ t LEFT JOIN orderline_ ol\n" +
                            "ON t.OrderlineID = ol.ID\n" +
                            "LEFT JOIN order_ o\n" +
                            "ON ol.OrderID = o.ID\n" +
                            "LEFT JOIN fund_ f\n" +
                            "ON ol.FundID = f.ID\n" +
                            "WHERE o.Status != 'ERROR';")
                    .rowMapper((rs, i) ->
                            new TempDataTableDTO(
                                    rs.getString("fundName"),
                                    rs.getDate("tradeDate"),
                                    rs.getBigDecimal("sum"),
                                    rs.getLong("orderID"),
                                    rs.getLong("orderlineID"))
                    )
                    .build();
        }

        @Bean
        JdbcBatchItemWriter<TempDataTableDTO> writeAllNeededDataToTempTable(DataSource dataSource) {
            return new JdbcBatchItemWriterBuilder<TempDataTableDTO>()
                    .dataSource(dataSource)
                    .sql("INSERT INTO temp_for_story3 (fund_name, trade_date, money, order_id, orderline_id) VALUES (:fundName, :tradeDate, :sum, :orderID, :orderLineID)")
                    .beanMapped()
                    .build();
        }

        @Bean
        Step writeTransactionsToTempTableStep(StepBuilderFactory stepBuilderFactory) {
            return stepBuilderFactory.get("write data to temp table")
                    .<TempDataTableDTO, TempDataTableDTO>chunk(100)
                    .reader(readTransactionsFromDatabase(null))
                    .writer(writeAllNeededDataToTempTable(null))
                    .build();
        }
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Configuration
    static class FillStockOrderWithData {
        @Bean
        JdbcCursorItemReader<TempDataTableDTO> readTransactionsFromTempTable(DataSource dataSource) {
            return new JdbcCursorItemReaderBuilder<TempDataTableDTO>()
                    .dataSource(dataSource)
                    .name("read transactions")
                    .sql("SELECT fund_name as fundName, trade_date as tradeDate, sum(money) as sum\n" +
                            "FROM temp_for_story3\n" +
                            "  GROUP BY fund_name")
                    .rowMapper((rs, i) ->
                            new TempDataTableDTO(
                                    rs.getString("fundName"),
                                    rs.getDate("tradeDate"),
                                    rs.getBigDecimal("sum"))
                    )
                    .build();
        }

        @Bean
        JdbcBatchItemWriter<TempDataTableDTO> writeStockOrderToDatabaseFromTempTable(DataSource dataSource) {
            return new JdbcBatchItemWriterBuilder<TempDataTableDTO>()
                    .dataSource(dataSource)
                    .sql("INSERT INTO stock_order (fundName, tradeDate, sum) VALUES (:fundName, :tradeDate, :sum)")
                    .beanMapped()
                    .build();
        }

        @Bean
        Step fillStockOrderStep(StepBuilderFactory stepBuilderFactory) {
            return stepBuilderFactory.get("write Stock Order Away")
                    .<TempDataTableDTO, TempDataTableDTO>chunk(100)
                    .reader(readTransactionsFromTempTable(null))
                    .writer(writeStockOrderToDatabaseFromTempTable(null))
                    .build();
        }
    }

}

