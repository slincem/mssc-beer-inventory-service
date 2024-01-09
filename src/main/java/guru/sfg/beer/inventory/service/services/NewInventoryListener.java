package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.RabbitMQConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.events.NewInventoryEvent;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.beer.inventory.service.web.mappers.BeerInventoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NewInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerInventoryMapper mapper;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.NEW_INVENTORY_QUEUE)
    public void listenNewInventory(NewInventoryEvent newInventoryEvent) {
        log.info("Received New Inventory Event: " + newInventoryEvent);

        BeerInventory beerInventory = mapper.beerDtoToBeerInventory(newInventoryEvent.getBeerDto());

        log.info("BeerInventory to save: " + beerInventory);
        beerInventoryRepository.save(beerInventory);
    }
}
