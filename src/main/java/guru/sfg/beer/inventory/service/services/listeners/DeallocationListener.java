package guru.sfg.beer.inventory.service.services.listeners;

import guru.sfg.beer.inventory.service.config.RabbitMQConfig;
import guru.sfg.beer.inventory.service.events.DeallocateBeerOrderRequest;
import guru.sfg.beer.inventory.service.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocationListener {

    private final AllocationService allocationService;

    @RabbitListener(queues = RabbitMQConfig.DEALLOCATE_BEER_ORDER_QUEUE)
    public void listenDeallocationRequest(DeallocateBeerOrderRequest deallocateBeerOrderRequest) {
        allocationService.deallocateOrder(deallocateBeerOrderRequest.getBeerOrderDto());
    }
}
