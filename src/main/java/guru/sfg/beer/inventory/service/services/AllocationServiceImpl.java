package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.events.BeerOrderDto;
import guru.sfg.beer.inventory.service.events.BeerOrderLineDto;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {

    private final BeerInventoryRepository beerInventoryRepository;
    @Override
    public Boolean allocateOrder(BeerOrderDto beerOrderDto) {

        log.debug("Allocating orderId: " + beerOrderDto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        beerOrderDto.getBeerOrderLines().forEach(beerOrderLine -> {
            int orderQuantity = beerOrderLine.getOrderQuantity() != null ? beerOrderLine.getOrderQuantity() : 0;
            int ordersAllocated = beerOrderLine.getQuantityAllocated() != null ? beerOrderLine.getQuantityAllocated() : 0;

            if(orderQuantity - ordersAllocated > 0) {
                allocateBeerOrderLine(beerOrderLine);
            }

            log.debug("Total Ordered: " + totalOrdered.get() + " Total Allocated: " + totalAllocated.get());

            totalOrdered.set(totalOrdered.get() + orderQuantity);
            totalAllocated.set(totalOrdered.get() + ordersAllocated);
        });

        return totalOrdered.get() == totalAllocated.get();
    }

    private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLine) {

        List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(beerOrderLine.getUpc());

        beerInventoryList.forEach(beerInventory -> {
            int inventoryOnHand = beerInventory.getQuantityOnHand() != null ? beerInventory.getQuantityOnHand() : 0;
            int orderQuantity = beerOrderLine.getOrderQuantity() != null ? beerOrderLine.getOrderQuantity() : 0;
            int ordersAllocated = beerOrderLine.getQuantityAllocated() != null ? beerOrderLine.getQuantityAllocated() : 0;
            int quantityToAllocate = orderQuantity - ordersAllocated;

            if(inventoryOnHand >= quantityToAllocate) {
                inventoryOnHand = inventoryOnHand - quantityToAllocate;
                beerOrderLine.setQuantityAllocated(orderQuantity);
                beerInventory.setQuantityOnHand(inventoryOnHand);

                beerInventoryRepository.save(beerInventory);
            } else if(inventoryOnHand > 0) { //partial allocation
                beerOrderLine.setQuantityAllocated(ordersAllocated + inventoryOnHand);
                beerInventory.setQuantityOnHand(0);

                //There are no more beers left in stock for this upc. (beer inventory reference)
                beerInventoryRepository.delete(beerInventory);
            }
        });
    }

    @Override
    public void deallocateOrder(BeerOrderDto beerOrderDto) {

        beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
            BeerInventory beerInventory = BeerInventory.builder()
                    .beerId(beerOrderLineDto.getBeerId())
                    .upc(beerOrderLineDto.getUpc())
                    .quantityOnHand(beerOrderLineDto.getOrderQuantity())
                    .build();

            BeerInventory savedInventory = beerInventoryRepository.save(beerInventory);

            log.debug("Saved Inventory for beer upc: " + savedInventory.getUpc() + " inventory id: " + savedInventory.getId());
        });
    }
}
