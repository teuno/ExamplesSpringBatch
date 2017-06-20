package BigFileMultiProcessUserStory1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
class ProcessConfiguration implements IMultiProcessConfiguration {

    private final static String QUEUE_NAME = "DataDTOQueue";
    private final static String EXCHANGE_NAME = "DataDTOExchange";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor
                .setMaxPoolSize(4); //standaard max int   waitingline for threads when queue is full. when queue is full and max threads is reached the tasks will be rejected
        log.info("maxpoolsize set to " + taskExecutor.getMaxPoolSize());

        taskExecutor.setCorePoolSize(4); //standaard 1
        log.info("poolsize set to " + taskExecutor.getCorePoolSize());

        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("teuno");
        factory.setPassword("teuno");
        return factory;
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public FanoutExchange tpExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, FanoutExchange tpexchange) {
        return BindingBuilder.bind(queue).to(tpexchange);
    }

    @Bean
    public AmqpTemplate ampqTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(EXCHANGE_NAME);
        template.setQueue(QUEUE_NAME);


        return template;
    }
}