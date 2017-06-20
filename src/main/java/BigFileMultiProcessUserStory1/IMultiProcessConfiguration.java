package BigFileMultiProcessUserStory1;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;

interface IMultiProcessConfiguration {
    @Bean
    TaskExecutor taskExecutor();

    @Bean
    ConnectionFactory connectionFactory();

    @Bean
    Queue queue();

    @Bean
    FanoutExchange tpExchange();

    @Bean
    Binding binding(Queue queue, FanoutExchange tpexchange);

    @Bean
    AmqpTemplate ampqTemplate(ConnectionFactory connectionFactory);
}
