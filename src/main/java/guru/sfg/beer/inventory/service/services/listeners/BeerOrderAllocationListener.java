package guru.sfg.beer.inventory.service.services.listeners;

import guru.sfg.beer.inventory.service.config.RabbitMQConfig;
import guru.sfg.beer.inventory.service.events.AllocateBeerOrderRequest;
import guru.sfg.beer.inventory.service.events.AllocateBeerOrderResult;
import guru.sfg.beer.inventory.service.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {

    private final RabbitTemplate rabbitTemplate;
    private final AllocationService allocationService;

    @RabbitListener(queues = RabbitMQConfig.ALLOCATE_BEER_ORDER_QUEUE)
    private void listenBeerOrderToAllocate(AllocateBeerOrderRequest allocateBeerOrderRequest) {

        AllocateBeerOrderResult.AllocateBeerOrderResultBuilder allocateBeerOrderResultBuilder = AllocateBeerOrderResult.builder().beerOrderDto(allocateBeerOrderRequest.getBeerOrderDto());

        try {
            boolean isBeerOrderAllocated = allocationService.allocateOrder(allocateBeerOrderRequest.getBeerOrderDto());
            allocateBeerOrderResultBuilder.pendingInventory(!isBeerOrderAllocated);
        } catch (Exception e) {
            log.error("Allocation failed for Order Id " + allocateBeerOrderRequest.getBeerOrderDto().getId());
            allocateBeerOrderResultBuilder.allocationError(true);
        }

        AllocateBeerOrderResult allocateBeerOrderResult = allocateBeerOrderResultBuilder.build();
        log.debug("Allocation Result: " + allocateBeerOrderResult);
        rabbitTemplate.convertAndSend(RabbitMQConfig.BEER_ORDER_EXCHANGE, RabbitMQConfig.BEER_ORDER_ALLOCATION_RESULT_ROUTING_KEY, allocateBeerOrderResult);

    }
}
