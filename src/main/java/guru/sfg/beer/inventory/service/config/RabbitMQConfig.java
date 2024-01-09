package guru.sfg.beer.inventory.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NEW_INVENTORY_QUEUE = "new-inventory-request";


    public static final String BEER_ORDER_EXCHANGE = "beer-order-exchange";
    public static final String ALLOCATE_BEER_ORDER_QUEUE = "allocate-beer-order-queue";
    public static final String ALLOCATE_BEER_ORDER_RESULT_QUEUE = "allocate-beer-oder-result-queue";

    public static final String BEER_ORDER_ALLOCATION_RESULT_ROUTING_KEY = "beer-order.allocate.result";

    @Bean
    Queue allocateResultQueue() {
        return new Queue(ALLOCATE_BEER_ORDER_RESULT_QUEUE, false);
    }

    @Bean
    TopicExchange beerOrderExchange() {
        return new TopicExchange(BEER_ORDER_EXCHANGE);
    }

    @Bean
    Binding binding(Queue allocateResultQueue, TopicExchange beerOrderExchange) {
        return BindingBuilder.bind(allocateResultQueue).to(beerOrderExchange).with(BEER_ORDER_ALLOCATION_RESULT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper springInternalObjectMapper) {
        springInternalObjectMapper.findAndRegisterModules();
        return new Jackson2JsonMessageConverter(springInternalObjectMapper);
    }

}
